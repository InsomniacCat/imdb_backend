package com.imdb.backend.service;

import com.imdb.backend.dto.CastMemberDTO;
import com.imdb.backend.dto.TitleDetailDTO;
import com.imdb.backend.entity.NameBasics;
import com.imdb.backend.entity.TitleBasics;
import com.imdb.backend.entity.TitleRatings;
import com.imdb.backend.entity.TitlePrincipals;
import com.imdb.backend.entity.TitleCrew;
import com.imdb.backend.repository.NameBasicsRepository;
import com.imdb.backend.repository.TitleBasicsRepository;
import com.imdb.backend.repository.TitleRatingsRepository;
import com.imdb.backend.repository.TitlePrincipalsRepository;
import com.imdb.backend.repository.TitleCrewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Title相关业务逻辑服务类
 * 处理电影/剧集信息的业务逻辑
 */
@Service
public class TitleService {

    @Autowired
    private TitleBasicsRepository titleBasicsRepository;

    @Autowired
    private TitleRatingsRepository titleRatingsRepository;

    @Autowired
    private TitlePrincipalsRepository titlePrincipalsRepository;

    @Autowired
    private TitleCrewRepository titleCrewRepository;

    @Autowired
    private NameBasicsRepository nameBasicsRepository;

    /**
     * 获取所有电影/剧集信息（分页）
     */
    public Page<TitleBasics> findAll(Pageable pageable) {
        return titleBasicsRepository.findAll(pageable);
    }

    /**
     * 根据ID获取电影详情（包括评分、主要演职员信息和演员姓名）
     */
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    /**
     * 根据ID获取电影详情（高效版：使用原生SQL Join）
     */
    public TitleDetailDTO getTitleDetails(String id) {
        // 1. 基础信息查询 (Basic Info)
        String baseSql = "SELECT primarytitle, originaltitle, isadult, startyear, endyear, runtimeminutes, genres " +
                "FROM title_basics WHERE tconst = ?";
        List<Map<String, Object>> baseResult = jdbcTemplate.queryForList(baseSql, id);

        if (baseResult.isEmpty()) {
            return null;
        }

        Map<String, Object> base = baseResult.get(0);

        // 2. 评分查询 (Ratings)
        String ratingSql = "SELECT averagerating, numvotes FROM title_ratings WHERE tconst = ?";
        List<Map<String, Object>> ratingResult = jdbcTemplate.queryForList(ratingSql, id);
        Double avgRating = null;
        Integer numVotes = null;
        if (!ratingResult.isEmpty()) {
            avgRating = (Double) ratingResult.get(0).get("averagerating");
            numVotes = (Integer) ratingResult.get(0).get("numvotes");
        }

        // 3. 演职员表查询 (Principals + Names) - 核心优化点
        String castSql = "SELECT p.nconst, n.primaryname, p.category, p.job, p.characters " +
                "FROM title_principals p " +
                "LEFT JOIN name_basics n ON p.nconst = n.nconst " +
                "WHERE p.tconst = ? " +
                "ORDER BY p.ordering";

        List<TitleDetailDTO.CastMemberDTO> castList = jdbcTemplate.query(castSql, (rs, rowNum) -> {
            String characters = rs.getString("characters");
            // 简单的JSON清洗，如果已经是jsonb则不需要，但为了兼容性保留
            if (characters != null) {
                characters = characters.replace("\\\"", "\"");
            }
            return new TitleDetailDTO.CastMemberDTO(
                    rs.getString("nconst"),
                    rs.getString("primaryname"),
                    rs.getString("category"),
                    rs.getString("job"),
                    characters);
        }, id);

        // 4. 组装最终 DTO
        TitleDetailDTO dto = new TitleDetailDTO();
        dto.setTconst(id);
        dto.setPrimaryTitle((String) base.get("primarytitle"));
        dto.setOriginalTitle((String) base.get("originaltitle"));
        dto.setIsAdult((Boolean) base.get("isadult"));
        dto.setStartYear((Integer) base.get("startyear"));
        dto.setEndYear((Integer) base.get("endyear"));
        dto.setRuntimeMinutes((Integer) base.get("runtimeminutes"));

        // Genres string to List
        String genresStr = (String) base.get("genres");
        if (genresStr != null) {
            dto.setGenres(Arrays.asList(genresStr.split(",")));
        }

        dto.setAverageRating(avgRating);
        dto.setNumVotes(numVotes);
        dto.setCast(castList); // 完美对接

        return dto;
    }

    /**
     * 获取Top Rated电影 (高分 + 必须有一定投票量)
     */
    public List<Map<String, Object>> getTopRated(int limit) {
        // 要求至少1000票才进榜单
        List<TitleRatings> topRatings = titleRatingsRepository.findTopRated(1000, PageRequest.of(0, limit));
        return enrichTitles(topRatings);
    }

    /**
     * 获取最热门电影 (按投票数排序)
     */
    public List<Map<String, Object>> getMostPopular(int limit) {
        List<TitleRatings> popRatings = titleRatingsRepository.findMostPopular(PageRequest.of(0, limit));
        return enrichTitles(popRatings);
    }

    /**
     * 辅助方法：将Ratings列表充实为包含TitleBasics信息的Map列表
     */
    private List<Map<String, Object>> enrichTitles(List<TitleRatings> ratingsList) {
        List<String> tconsts = ratingsList.stream().map(TitleRatings::getTconst).collect(Collectors.toList());
        List<TitleBasics> basicsList = titleBasicsRepository.findAllById(tconsts);
        Map<String, TitleBasics> basicsMap = basicsList.stream()
                .collect(Collectors.toMap(TitleBasics::getTconst, b -> b));

        List<Map<String, Object>> results = new ArrayList<>();
        for (TitleRatings r : ratingsList) {
            TitleBasics b = basicsMap.get(r.getTconst());
            if (b != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("tconst", b.getTconst());
                map.put("primaryTitle", b.getPrimaryTitle());
                map.put("originalTitle", b.getOriginalTitle());
                map.put("startYear", b.getStartYear());
                map.put("titleType", b.getTitleType());
                map.put("genres", b.getGenres());
                map.put("averageRating", r.getAverageRating());
                map.put("numVotes", r.getNumVotes());
                // Poster URL logic could go here if we had it
                results.add(map);
            }
        }
        return results;
    }

    /**
     * 搜索电影/剧集（按标题）
     * 优化版本：使用分页限制 + 批量查询评分
     */
    public List<Map<String, Object>> searchByTitle(String query) {
        // 使用分页限制，最多返回50条
        Pageable limit = PageRequest.of(0, 50);

        // 优化：优先使用全文检索 (Full Text Search)
        // 如果查询词太短（例如1-2个字母），全文检索可能效果不佳或性能开销大，
        // 但对于 "Star Wars" 这样的词，性能是数量级提升。
        // 这里我们对所有查询都启用全文检索，因为我们创建了GIN索引。
        Page<TitleBasics> page = titleBasicsRepository.searchByFullText(query, limit);
        List<TitleBasics> titles = page.getContent();

        // 如果全文检索没结果（有的词可能被当做停用词过滤了），可以由前端决定是否重试或提示
        // 也可以在这里fallback到旧的LIKE查询，但LIKE查询在大数据量下有风险，暂时只用FTS。
        if (titles.isEmpty() && query.length() > 3) {
            // Fallback logic could be added here if really needed
        }

        // 批量查询所有评分（避免N+1问题）
        List<String> tconsts = titles.stream()
                .map(TitleBasics::getTconst)
                .collect(Collectors.toList());

        List<TitleRatings> ratingsList = titleRatingsRepository.findAllById(tconsts);
        Map<String, TitleRatings> ratingsMap = ratingsList.stream()
                .collect(Collectors.toMap(TitleRatings::getTconst, r -> r));

        // 组装结果
        List<Map<String, Object>> results = new ArrayList<>();
        for (TitleBasics title : titles) {
            Map<String, Object> titleMap = new HashMap<>();
            titleMap.put("tconst", title.getTconst());
            titleMap.put("titleType", title.getTitleType());
            titleMap.put("primaryTitle", title.getPrimaryTitle());
            titleMap.put("startYear", title.getStartYear());
            titleMap.put("genres", title.getGenres());

            // 从Map中获取评分（如果存在）
            TitleRatings ratings = ratingsMap.get(title.getTconst());
            if (ratings != null) {
                titleMap.put("averageRating", ratings.getAverageRating());
                titleMap.put("numVotes", ratings.getNumVotes());
            }

            results.add(titleMap);
        }
        return results;
    }

    // Existing methods below...

    /**
     * 保存电影/剧集信息
     */
    public TitleBasics save(TitleBasics title) {
        if (title.getTconst() != null && titleBasicsRepository.existsByTconst(title.getTconst())) {
            throw new IllegalArgumentException("电影ID已存在: " + title.getTconst());
        }
        return titleBasicsRepository.save(title);
    }

    /**
     * 更新电影/剧集信息
     */
    public TitleBasics update(String id, TitleBasics title) {
        if (!titleBasicsRepository.existsById(id)) {
            return null;
        }
        title.setTconst(id);
        return titleBasicsRepository.save(title);
    }

    /**
     * 删除电影/剧集信息
     */
    public boolean delete(String id) {
        if (!titleBasicsRepository.existsById(id)) {
            return false;
        }
        try {
            titlePrincipalsRepository.deleteAll(titlePrincipalsRepository.findByIdTconst(id));
            titleCrewRepository.deleteById(id);
            titleRatingsRepository.deleteById(id);
            titleBasicsRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("删除电影信息失败: " + e.getMessage());
        }
    }

    public Page<TitleBasics> advancedSearch(String title, String titleType, Boolean isAdult,
            Integer startYear, Integer endYear, Integer minRuntime, Integer maxRuntime, Pageable pageable) {
        return titleBasicsRepository.searchByMultipleCriteria(
                title, titleType, isAdult, startYear, endYear, minRuntime, maxRuntime, pageable);
    }

    public List<TitleCrew> findByDirectorsContaining(String directorId) {
        return titleCrewRepository.findByDirectorsContaining(directorId);
    }

    public List<TitleCrew> findByWritersContaining(String writerId) {
        return titleCrewRepository.findByWritersContaining(writerId);
    }

    public Map<String, Long> getTitleTypeStats() {
        List<Object[]> results = titleBasicsRepository.countByTitleType();
        return results.stream().collect(Collectors.toMap(row -> (String) row[0], row -> (Long) row[1]));
    }

    public Map<Integer, Long> getTitleYearStats(Integer startYear, Integer endYear) {
        List<Object[]> results = titleBasicsRepository.countByStartYear(startYear, endYear);
        return results.stream().filter(row -> row[0] != null)
                .collect(Collectors.toMap(row -> (Integer) row[0], row -> (Long) row[1]));
    }
}