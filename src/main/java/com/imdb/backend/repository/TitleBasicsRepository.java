package com.imdb.backend.repository;

import com.imdb.backend.entity.TitleBasics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TitleBasics实体的数据访问接口
 * 提供对title_basics表的基本CRUD操作和自定义查询方法
 */
@Repository
public interface TitleBasicsRepository extends JpaRepository<TitleBasics, String> {

    List<TitleBasics> findByPrimaryTitleContainingIgnoreCase(String title);

    /**
     * 复合查询：根据标题、类型、年份范围、运行时长等条件
     * 返回 Page 对象以支持服务端分页
     */
    @Query("SELECT t FROM TitleBasics t WHERE " +
            "(:title IS NULL OR LOWER(t.primaryTitle) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:titleType IS NULL OR t.titleType = :titleType) AND " +
            "(:isAdult IS NULL OR t.isAdult = :isAdult) AND " +
            "(:startYear IS NULL OR t.startYear >= :startYear) AND " +
            "(:endYear IS NULL OR t.startYear <= :endYear) AND " +
            "(:minRuntime IS NULL OR t.runtimeMinutes >= :minRuntime) AND " +
            "(:maxRuntime IS NULL OR t.runtimeMinutes <= :maxRuntime)")
    Page<TitleBasics> searchByMultipleCriteria(
            @Param("title") String title,
            @Param("titleType") String titleType,
            @Param("isAdult") Boolean isAdult,
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear,
            @Param("minRuntime") Integer minRuntime,
            @Param("maxRuntime") Integer maxRuntime,
            Pageable pageable);

    /**
     * 统计类型数量
     */
    @Query("SELECT t.titleType, COUNT(t) FROM TitleBasics t GROUP BY t.titleType")
    List<Object[]> countByTitleType();

    /**
     * 统计年份数量
     */
    @Query("SELECT t.startYear, COUNT(t) FROM TitleBasics t " +
            "WHERE (:startYear IS NULL OR t.startYear >= :startYear) AND " +
            "(:endYear IS NULL OR t.startYear <= :endYear) " +
            "GROUP BY t.startYear")
    List<Object[]> countByStartYear(
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear);

    boolean existsByTconst(String tconst);
}