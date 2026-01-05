package com.imdb.backend.controller;

import com.imdb.backend.dto.TitleDetailDTO;
import com.imdb.backend.entity.TitleBasics;
import com.imdb.backend.entity.TitleCrew;
import com.imdb.backend.service.TitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TitleBasics实体的REST控制器
 * 提供对电影/剧集信息的完整CRUD操作和搜索功能
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/titles")
public class TitleController {

    @Autowired
    private TitleService titleService;

    /**
     * 获取所有电影/剧集信息（支持分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTitles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "tconst") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TitleBasics> titles = titleService.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("titles", titles.getContent());
        response.put("currentPage", titles.getNumber());
        response.put("totalItems", titles.getTotalElements());
        response.put("totalPages", titles.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取Top Rated (高分榜)
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Map<String, Object>>> getTopRated(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(titleService.getTopRated(limit));
    }

    /**
     * 获取Most Popular (热门榜)
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Map<String, Object>>> getMostPopular(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(titleService.getMostPopular(limit));
    }

    /**
     * 根据ID获取电影详情 (Enriched)
     */
    @GetMapping("/{id}")
    public ResponseEntity<TitleDetailDTO> getTitleById(@PathVariable String id) {
        TitleDetailDTO titleDetails = titleService.getTitleDetails(id);
        if (titleDetails != null) {
            return ResponseEntity.ok(titleDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Simple Search
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchByTitle(@RequestParam String q) {
        return ResponseEntity.ok(titleService.searchByTitle(q));
    }

    /**
     * Create
     */
    @PostMapping
    public ResponseEntity<TitleBasics> createTitle(@RequestBody TitleBasics title) {
        return ResponseEntity.status(HttpStatus.CREATED).body(titleService.save(title));
    }

    /**
     * Update
     */
    @PutMapping("/{id}")
    public ResponseEntity<TitleBasics> updateTitle(@PathVariable String id, @RequestBody TitleBasics title) {
        TitleBasics updated = titleService.update(id, title);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    /**
     * Delete
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTitle(@PathVariable String id) {
        return titleService.delete(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * Advanced Search
     */
    @GetMapping("/advanced-search")
    public ResponseEntity<Map<String, Object>> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String titleType,
            @RequestParam(required = false) Boolean isAdult,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear,
            @RequestParam(required = false) Integer minRuntime,
            @RequestParam(required = false) Integer maxRuntime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "tconst") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TitleBasics> results = titleService.advancedSearch(
                title, titleType, isAdult, startYear, endYear, minRuntime, maxRuntime, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("titles", results.getContent());
        response.put("currentPage", results.getNumber());
        response.put("totalItems", results.getTotalElements());
        response.put("totalPages", results.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // Director/Stats endpoints remain similar...
    @GetMapping("/by-director/{directorId}")
    public ResponseEntity<List<TitleCrew>> getByDirector(@PathVariable String directorId) {
        return ResponseEntity.ok(titleService.findByDirectorsContaining(directorId));
    }

    @GetMapping("/by-writer/{writerId}")
    public ResponseEntity<List<TitleCrew>> getByWriter(@PathVariable String writerId) {
        return ResponseEntity.ok(titleService.findByWritersContaining(writerId));
    }

    @GetMapping("/stats/types")
    public ResponseEntity<Map<String, Long>> getTitleTypeStats() {
        return ResponseEntity.ok(titleService.getTitleTypeStats());
    }

    @GetMapping("/stats/years")
    public ResponseEntity<Map<Integer, Long>> getTitleYearStats(
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear) {
        return ResponseEntity.ok(titleService.getTitleYearStats(startYear, endYear));
    }
}