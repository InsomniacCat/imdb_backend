package com.imdb.backend.service;

import com.imdb.backend.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataImportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String DATABASE_PATH = "c:\\Users\\Oliveira\\Desktop\\Work\\DB\\imdb\\database\\";
    private static final int BATCH_SIZE = 1000; // Reduced to 1000 to avoid OOM

    public String importData(String fileType, boolean testMode) {
        String fileName;
        switch (fileType) {
            case "basics":
                fileName = "title.basics.tsv";
                break;
            case "ratings":
                fileName = "title.ratings.tsv";
                break;
            case "names":
                fileName = "name.basics.tsv";
                break;
            case "crew":
                fileName = "title.crew.tsv";
                break;
            case "principals":
                fileName = "title.principals.tsv";
                break;
            case "episode":
                fileName = "title.episode.tsv";
                break;
            case "akas":
                fileName = "title.akas.tsv";
                break;
            default:
                return "Unknown file type: " + fileType;
        }

        String filePath = DATABASE_PATH + fileName;
        int limit = testMode ? 100 : Integer.MAX_VALUE;
        int count = 0;
        int skipped = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Read header
            if (line == null)
                return "Empty file";

            List<Object> batch = new ArrayList<>();

            // Logic: keep reading if limit is MAX (import all) OR if current progress
            // (count + pending batch) is less than limit
            while ((line = br.readLine()) != null && (limit == Integer.MAX_VALUE || (count + batch.size()) < limit)) {
                // PostgreSql rejects 0x00, sanitize it
                line = line.replace("\u0000", "");

                try {
                    // Use -1 limit to keep trailing empty strings
                    String[] parts = line.split("\t", -1);
                    Object entity = null;

                    switch (fileType) {
                        case "basics":
                            entity = parseTitleBasics(parts);
                            break;
                        case "ratings":
                            entity = parseTitleRatings(parts);
                            break;
                        case "names":
                            entity = parseNameBasics(parts);
                            break;
                        case "crew":
                            entity = parseTitleCrew(parts);
                            break;
                        case "principals":
                            entity = parseTitlePrincipals(parts);
                            break;
                        case "episode":
                            entity = parseTitleEpisode(parts);
                            break;
                        case "akas":
                            entity = parseTitleAkas(parts);
                            break;
                    }

                    if (entity != null) {
                        batch.add(entity);
                    } else {
                        skipped++;
                        // Log first few skips to debug
                        if (skipped <= 5) {
                            System.err.println("Skipped line (parsing result null): " + line);
                        }
                    }

                    if (batch.size() >= BATCH_SIZE) {
                        saveBatch(batch, fileType);
                        count += batch.size();
                        batch.clear();
                        System.out.println("Imported " + count + " records for " + fileType);
                    }
                } catch (Exception e) {
                    skipped++;
                    if (skipped <= 10) {
                        System.err.println("Error parsing/saving line: " + line + " Error: " + e.getMessage());
                    }
                    // Critical Fix: If saveBatch failed, batch is still full. Clear it to prevent
                    // infinite loop.
                    if (batch.size() >= BATCH_SIZE) {
                        System.err.println("Batch failed. Clearing " + batch.size() + " records to proceed.");
                        batch.clear();
                    }
                }
            }

            if (!batch.isEmpty()) {
                saveBatch(batch, fileType);
                count += batch.size();
            }

            return String.format("Import completed for %s. Imported: %d, Skipped: %d", fileType, count, skipped);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error reading file: " + e.getMessage();
        }
    }

    @Transactional
    protected void saveBatch(List<Object> batch, String fileType) {
        if (batch.isEmpty())
            return;

        String sql = "";
        switch (fileType) {
            case "basics":
                // 数据库实际列名为全小写 (StandardImpl + Postgres 默认行为)
                sql = "INSERT INTO title_basics (tconst, titletype, primarytitle, originaltitle, isadult, startyear, endyear, runtimeminutes, genres) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (tconst) DO NOTHING";
                jdbcTemplate.batchUpdate(sql, batch, BATCH_SIZE, (PreparedStatement ps, Object o) -> {
                    TitleBasics t = (TitleBasics) o;
                    ps.setString(1, t.getTconst());
                    ps.setString(2, t.getTitleType());
                    ps.setString(3, t.getPrimaryTitle());
                    ps.setString(4, t.getOriginalTitle());
                    setBool(ps, 5, t.getIsAdult());
                    setInt(ps, 6, t.getStartYear());
                    setInt(ps, 7, t.getEndYear());
                    setInt(ps, 8, t.getRuntimeMinutes());
                    ps.setString(9, listToCsv(t.getGenres()));
                });
                break;
            case "ratings":
                sql = "INSERT INTO title_ratings (tconst, averagerating, numvotes) VALUES (?, ?, ?) ON CONFLICT (tconst) DO NOTHING";
                jdbcTemplate.batchUpdate(sql, batch, BATCH_SIZE, (PreparedStatement ps, Object o) -> {
                    TitleRatings r = (TitleRatings) o;
                    ps.setString(1, r.getTconst());
                    setDouble(ps, 2, r.getAverageRating());
                    setInt(ps, 3, r.getNumVotes());
                });
                break;
            case "names":
                sql = "INSERT INTO name_basics (nconst, primaryname, birthyear, deathyear, primaryprofession, knownfortitles) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (nconst) DO NOTHING";
                jdbcTemplate.batchUpdate(sql, batch, BATCH_SIZE, (PreparedStatement ps, Object o) -> {
                    NameBasics n = (NameBasics) o;
                    ps.setString(1, n.getNconst());
                    ps.setString(2, n.getPrimaryName());
                    setInt(ps, 3, n.getBirthYear());
                    setInt(ps, 4, n.getDeathYear());
                    ps.setString(5, listToCsv(n.getPrimaryProfession()));
                    ps.setString(6, listToCsv(n.getKnownForTitles()));
                });
                break;
            case "crew":
                sql = "INSERT INTO title_crew (tconst, directors, writers) VALUES (?, ?, ?) ON CONFLICT (tconst) DO NOTHING";
                jdbcTemplate.batchUpdate(sql, batch, BATCH_SIZE, (PreparedStatement ps, Object o) -> {
                    TitleCrew c = (TitleCrew) o;
                    ps.setString(1, c.getTconst());
                    ps.setString(2, listToCsv(c.getDirectors()));
                    ps.setString(3, listToCsv(c.getWriters()));
                });
                break;
            case "principals":
                sql = "INSERT INTO title_principals (tconst, ordering, nconst, category, job, characters) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (tconst, ordering) DO NOTHING";
                jdbcTemplate.batchUpdate(sql, batch, BATCH_SIZE, (PreparedStatement ps, Object o) -> {
                    TitlePrincipals p = (TitlePrincipals) o;
                    ps.setString(1, p.getId().getTconst());
                    setInt(ps, 2, p.getId().getOrdering());
                    ps.setString(3, p.getNconst());
                    ps.setString(4, p.getCategory());
                    ps.setString(5, p.getJob());
                    ps.setString(6, listToJson(p.getCharacters()));
                });
                break;
            case "episode":
                // 假设 title_episode 也是全小写列名 (parenttconst, seasonnumber, episodenumber)
                sql = "INSERT INTO title_episode (tconst, parenttconst, seasonnumber, episodenumber) VALUES (?, ?, ?, ?) ON CONFLICT (tconst) DO NOTHING";
                jdbcTemplate.batchUpdate(sql, batch, BATCH_SIZE, (PreparedStatement ps, Object o) -> {
                    TitleEpisode e = (TitleEpisode) o;
                    ps.setString(1, e.getTconst());
                    ps.setString(2, e.getParentTconst());
                    setInt(ps, 3, e.getSeasonNumber());
                    setInt(ps, 4, e.getEpisodeNumber());
                });
                break;
            case "akas":
                // 假设 title_akas 也是全小写 (titleid, ordering, title, region, language, types,
                // attributes, isoriginaltitle)
                sql = "INSERT INTO title_akas (titleid, ordering, title, region, language, types, attributes, isoriginaltitle) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (titleid, ordering) DO NOTHING";
                jdbcTemplate.batchUpdate(sql, batch, BATCH_SIZE, (PreparedStatement ps, Object o) -> {
                    TitleAkas a = (TitleAkas) o;
                    ps.setString(1, a.getTitleId());
                    setInt(ps, 2, a.getOrdering());
                    ps.setString(3, a.getTitle());
                    ps.setString(4, a.getRegion());
                    ps.setString(5, a.getLanguage());
                    ps.setString(6, a.getTypes());
                    ps.setString(7, a.getAttributes());
                    setBool(ps, 8, a.getIsOriginalTitle());
                });
                break;
        }
    }

    // Helper methods for JDBC
    private void setInt(PreparedStatement ps, int paramIndex, Integer value) throws java.sql.SQLException {
        if (value == null)
            ps.setNull(paramIndex, java.sql.Types.INTEGER);
        else
            ps.setInt(paramIndex, value);
    }

    private void setDouble(PreparedStatement ps, int paramIndex, Double value) throws java.sql.SQLException {
        if (value == null)
            ps.setNull(paramIndex, java.sql.Types.DOUBLE);
        else
            ps.setDouble(paramIndex, value);
    }

    private void setBool(PreparedStatement ps, int paramIndex, Boolean value) throws java.sql.SQLException {
        if (value == null)
            ps.setNull(paramIndex, java.sql.Types.BOOLEAN);
        else
            ps.setBoolean(paramIndex, value);
    }

    private String listToCsv(List<String> list) {
        if (list == null || list.isEmpty())
            return null;
        return String.join(",", list);
    }

    private String listToJson(List<String> list) {
        if (list == null || list.isEmpty())
            return null;
        // Simple JSON array construction
        return "[" + list.stream()
                .map(s -> "\"" + s.replace("\"", "\\\"") + "\"")
                .collect(Collectors.joining(",")) + "]";
    }

    // === Parsing Methods (Kept simplified) ===

    private String getVal(String val) {
        return "\\N".equals(val) ? null : val;
    }

    private Integer getInt(String val) {
        if ("\\N".equals(val) || val == null || val.isEmpty())
            return null;
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return null;
        }
    }

    private Double getDouble(String val) {
        if ("\\N".equals(val) || val == null || val.isEmpty())
            return null;
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean getBool(String val) {
        if ("\\N".equals(val) || val == null)
            return null;
        return "1".equals(val);
    }

    private List<String> getList(String val) {
        if ("\\N".equals(val) || val == null || val.isEmpty())
            return null;
        return Arrays.asList(val.split(","));
    }

    private List<String> getJsonList(String val) {
        if ("\\N".equals(val) || val == null || val.isEmpty())
            return null;
        String clean = val.replace("[\"", "").replace("\"]", "").replace("\"", "");
        return Arrays.asList(clean.split(","));
    }

    private TitleBasics parseTitleBasics(String[] p) {
        if (p.length < 9)
            return null;
        return new TitleBasics(getVal(p[0]), getVal(p[1]), getVal(p[2]), getVal(p[3]), getBool(p[4]), getInt(p[5]),
                getInt(p[6]), getInt(p[7]), getList(p[8]));
    }

    private TitleRatings parseTitleRatings(String[] p) {
        if (p.length < 3)
            return null;
        TitleRatings t = new TitleRatings();
        t.setTconst(getVal(p[0]));
        t.setAverageRating(getDouble(p[1]));
        t.setNumVotes(getInt(p[2]));
        return t;
    }

    private NameBasics parseNameBasics(String[] p) {
        if (p.length < 6)
            return null;

        String nconst = getVal(p[0]);
        String primaryName = getVal(p[1]);

        // Fix: Skip row if primaryName is null (prevents NOT NULL constraint violation)
        if (nconst == null || primaryName == null) {
            return null;
        }

        NameBasics n = new NameBasics();
        n.setNconst(nconst);
        n.setPrimaryName(primaryName);
        n.setBirthYear(getInt(p[2]));
        n.setDeathYear(getInt(p[3]));
        n.setPrimaryProfession(getList(p[4]));
        n.setKnownForTitles(getList(p[5]));
        return n;
    }

    private TitleCrew parseTitleCrew(String[] p) {
        if (p.length < 3)
            return null;
        TitleCrew c = new TitleCrew();
        c.setTconst(getVal(p[0]));
        c.setDirectors(getList(p[1]));
        c.setWriters(getList(p[2]));
        return c;
    }

    private TitlePrincipals parseTitlePrincipals(String[] p) {
        if (p.length < 6)
            return null;
        TitlePrincipals tp = new TitlePrincipals();
        TitlePrincipalsId id = new TitlePrincipalsId();
        id.setTconst(getVal(p[0]));
        id.setOrdering(getInt(p[1]));
        tp.setId(id);
        tp.setNconst(getVal(p[2]));
        tp.setCategory(getVal(p[3]));
        tp.setJob(getVal(p[4]));
        tp.setCharacters(getJsonList(p[5]));
        return tp;
    }

    private TitleEpisode parseTitleEpisode(String[] p) {
        if (p.length < 4)
            return null;
        TitleEpisode te = new TitleEpisode();
        te.setTconst(getVal(p[0]));
        te.setParentTconst(getVal(p[1]));
        te.setSeasonNumber(getInt(p[2]));
        te.setEpisodeNumber(getInt(p[3]));
        return te;
    }

    private TitleAkas parseTitleAkas(String[] p) {
        if (p.length < 8)
            return null;
        TitleAkas ta = new TitleAkas();
        ta.setTitleId(getVal(p[0]));
        ta.setOrdering(getInt(p[1]));
        ta.setTitle(getVal(p[2]));
        ta.setRegion(getVal(p[3]));
        ta.setLanguage(getVal(p[4]));
        ta.setTypes(getVal(p[5]));
        ta.setAttributes(getVal(p[6]));
        ta.setIsOriginalTitle(getBool(p[7]));
        return ta;
    }
}
