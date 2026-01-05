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
    public TitleDetailDTO getTitleDetails(String id) {
        Optional<TitleBasics> titleOpt = titleBasicsRepository.findById(id);
        if (!titleOpt.isPresent()) {
            return null;
        }

        TitleBasics title = titleOpt.get();
        TitleRatings ratings = titleRatingsRepository.findById(id).orElse(null);
        TitleCrew crew = titleCrewRepository.findById(id).orElse(null);

        // 1. 获取所有主要演职人员 (Principals)
        List<TitlePrincipals> principals = titlePrincipalsRepository.findByIdTconst(id);

        // 2. 提取所有的 nconst
        List<String> nconsts = principals.stream()
                .map(TitlePrincipals::getNconst)
                .collect(Collectors.toList());

        // 3. 批量查询 Names
        List<NameBasics> names = nameBasicsRepository.findAllById(nconsts);
        Map<String, String> nameMap = names.stream()
                .collect(Collectors.toMap(NameBasics::getNconst, NameBasics::getPrimaryName));

        // 4. 组装 CastMemberDTO
        List<CastMemberDTO> castList = principals.stream()
                .map(p -> new CastMemberDTO(
                        p.getNconst(),
                        nameMap.getOrDefault(p.getNconst(), "Unknown"),
                        p.getCategory(),
                        p.getJob(),
                        p.getCharacters()))
                .collect(Collectors.toList());

        return new TitleDetailDTO(title, ratings, crew, castList);
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
     */
    public List<Map<String, Object>> searchByTitle(String query) {
        // Cap results to avoid massive lists
        List<TitleBasics> titles = titleBasicsRepository.findByPrimaryTitleContainingIgnoreCase(query);
        if (titles.size() > 50) {
            titles = titles.subList(0, 50);
        }

        List<Map<String, Object>> results = new ArrayList<>();
        for (TitleBasics title : titles) {
            Map<String, Object> titleMap = new HashMap<>();
            titleMap.put("tconst", title.getTconst());
            titleMap.put("titleType", title.getTitleType());
            titleMap.put("primaryTitle", title.getPrimaryTitle());
            titleMap.put("startYear", title.getStartYear());
            titleMap.put("genres", title.getGenres());

            // 查询评分（可选：为了搜索结果也显示分数）
            Optional<TitleRatings> ratingsOpt = titleRatingsRepository.findById(title.getTconst());
            if (ratingsOpt.isPresent()) {
                titleMap.put("averageRating", ratingsOpt.get().getAverageRating());
                titleMap.put("numVotes", ratingsOpt.get().getNumVotes());
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