package com.imdb.backend.repository;

import com.imdb.backend.entity.TitleRatings;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TitleRatings实体的数据访问接口
 * 提供对title_ratings表的基本CRUD操作和自定义查询方法
 */
@Repository
public interface TitleRatingsRepository extends JpaRepository<TitleRatings, String> {

    /**
     * 根据平均评分范围查找
     */
    List<TitleRatings> findByAverageRatingBetween(Double minRating, Double maxRating);

    /**
     * 根据投票数范围查找
     */
    List<TitleRatings> findByNumVotesGreaterThanEqual(Integer minVotes);

    /**
     * 查找评分最高的记录 (Top 250 style: high rating + significant votes)
     * 使用 Pageable 来控制 limit
     */
    @Query("SELECT t FROM TitleRatings t WHERE t.numVotes >= :minVotes ORDER BY t.averageRating DESC, t.numVotes DESC")
    List<TitleRatings> findTopRated(@Param("minVotes") Integer minVotes, Pageable pageable);

    /**
     * 查找最热门的记录 (Most Popular: sorted by votes)
     */
    @Query("SELECT t FROM TitleRatings t ORDER BY t.numVotes DESC")
    List<TitleRatings> findMostPopular(Pageable pageable);

    /**
     * 根据评分和投票数筛选（高评分且高投票数）
     */
    List<TitleRatings> findByAverageRatingGreaterThanEqualAndNumVotesGreaterThanEqual(
            Double minRating, Integer minVotes);

    /**
     * 检查tconst是否存在
     */
    boolean existsByTconst(String tconst);
}