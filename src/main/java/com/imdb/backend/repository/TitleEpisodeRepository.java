package com.imdb.backend.repository;

import com.imdb.backend.entity.TitleEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TitleEpisode实体的数据访问接口
 * 提供对title_episode表的基本CRUD操作和自定义查询方法
 */
@Repository
public interface TitleEpisodeRepository extends JpaRepository<TitleEpisode, String> {
    
    /**
     * 根据父级作品ID查找所有剧集
     */
    List<TitleEpisode> findByParentTconst(String parentTconst);
    
    /**
     * 根据季号查找
     */
    List<TitleEpisode> findBySeasonNumber(Integer seasonNumber);
    
    /**
     * 根据季号和集号查找
     */
    List<TitleEpisode> findBySeasonNumberAndEpisodeNumber(Integer seasonNumber, Integer episodeNumber);
    
    /**
     * 根据集号查找
     */
    List<TitleEpisode> findByEpisodeNumber(Integer episodeNumber);
    
    /**
     * 检查tconst是否存在
     */
    boolean existsByTconst(String tconst);
}