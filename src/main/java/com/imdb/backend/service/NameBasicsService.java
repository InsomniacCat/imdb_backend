package com.imdb.backend.service;

import com.imdb.backend.dto.NameCareerDTO;
import com.imdb.backend.entity.NameBasics;
import com.imdb.backend.repository.NameBasicsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NameBasicsService {

    private final NameBasicsRepository repo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public NameBasicsService(NameBasicsRepository repo) {
        this.repo = repo;
    }

    public Page<NameBasics> listAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public NameBasics findById(String id) {
        return repo.findById(id).orElse(null);
    }

    public NameBasics save(NameBasics nb) {
        return repo.save(nb);
    }

    public List<NameBasics> searchByName(String q) {
        Pageable limit = PageRequest.of(0, 50);
        return repo.findByPrimaryNameContainingIgnoreCase(q, limit);
    }

    public void deleteById(String id) {
        repo.deleteById(id);
    }

    /**
     * 获取影人生涯数据分析 (图表数据源)
     * 包括：评分随年份变化趋势、参与作品的类型分布
     */
    public NameCareerDTO getCareerAnalytics(String nconst) {
        // 1. 获取基本信息
        NameBasics basic = findById(nconst);
        if (basic == null)
            return null;

        NameCareerDTO dto = new NameCareerDTO();
        dto.setBasicInfo(new NameCareerDTO.BasicInfo(
                basic.getNconst(),
                basic.getPrimaryName(),
                basic.getBirthYear(),
                basic.getDeathYear(),
                String.join(", ", basic.getPrimaryProfession()),
                basic.getKnownForTitles()));

        // 2. 聚合：按年份统计平均分 (Career Trajectory)
        String trendSql = "SELECT b.startyear, AVG(r.averagerating) as score, COUNT(*) as cnt " +
                "FROM title_principals p " +
                "JOIN title_basics b ON p.tconst = b.tconst " +
                "JOIN title_ratings r ON b.tconst = r.tconst " +
                "WHERE p.nconst = ? AND b.startyear IS NOT NULL " +
                "GROUP BY b.startyear " +
                "ORDER BY b.startyear";

        List<NameCareerDTO.YearlyRating> yearlyStats = jdbcTemplate.query(trendSql,
                (rs, rowNum) -> new NameCareerDTO.YearlyRating(
                        rs.getInt("startyear"),
                        Math.round(rs.getDouble("score") * 10.0) / 10.0,
                        rs.getInt("cnt")),
                nconst);
        dto.setYearlyRatings(yearlyStats);

        // 3. 聚合：按类型统计 (Genre Analysis)
        // BUG FIX: Removed 'r.' from outer select list
        String genreSql = "SELECT genre, COUNT(*) as cnt, AVG(averagerating) as score " +
                "FROM ( " +
                "  SELECT regexp_split_to_table(b.genres, ',') as genre, r.averagerating " +
                "  FROM title_principals p " +
                "  JOIN title_basics b ON p.tconst = b.tconst " +
                "  JOIN title_ratings r ON b.tconst = r.tconst " +
                "  WHERE p.nconst = ? " +
                ") sub " +
                "WHERE genre IS NOT NULL AND genre != '' AND genre != '\\\\N' " +
                "GROUP BY genre " +
                "ORDER BY cnt DESC " +
                "LIMIT 10";

        List<NameCareerDTO.GenreStat> genreStats = jdbcTemplate.query(genreSql,
                (rs, rowNum) -> new NameCareerDTO.GenreStat(
                        rs.getString("genre"),
                        rs.getInt("cnt"),
                        Math.round(rs.getDouble("score") * 10.0) / 10.0),
                nconst);
        dto.setGenreStats(genreStats);

        // 4. 查询代表作详情 (Famous Works)
        List<String> kf = basic.getKnownForTitles();
        if (kf != null && !kf.isEmpty()) {
            String placeholders = String.join(",", java.util.Collections.nCopies(kf.size(), "?"));
            String famousSql = "SELECT b.tconst, b.primarytitle, b.startyear, r.averagerating " +
                    "FROM title_basics b " +
                    "LEFT JOIN title_ratings r ON b.tconst = r.tconst " +
                    "WHERE b.tconst IN (" + placeholders + ")";

            List<NameCareerDTO.WorkSummary> works = jdbcTemplate.query(famousSql,
                    (rs, rowNum) -> new NameCareerDTO.WorkSummary(
                            rs.getString("tconst"),
                            rs.getString("primarytitle"), // DB column is lowercase often
                            rs.getInt("startyear"),
                            rs.getDouble("averagerating")),
                    kf.toArray());

            dto.setFamousWorks(works);
        }

        return dto;
    }
}