package com.imdb.backend.repository;

import com.imdb.backend.entity.TitleAkas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TitleAkas实体的数据访问接口
 * 提供对title_akas表的基本CRUD操作和自定义查询方法
 */
@Repository
public interface TitleAkasRepository extends JpaRepository<TitleAkas, com.imdb.backend.entity.TitleAkasId> {
    
    /**
     * 根据titleId查找所有别名
     */
    List<TitleAkas> findByTitleId(String titleId);
    
    /**
     * 根据地区查找
     */
    List<TitleAkas> findByRegion(String region);
    
    /**
     * 根据语言查找
     */
    List<TitleAkas> findByLanguage(String language);
    
    /**
     * 根据标题模糊搜索
     */
    List<TitleAkas> findByTitleContainingIgnoreCase(String title);
    
    /**
     * 根据titleId和区域查找
     */
    List<TitleAkas> findByTitleIdAndRegion(String titleId, String region);
    
    /**
     * 根据是否为原版标题查找
     */
    List<TitleAkas> findByIsOriginalTitle(Boolean isOriginalTitle);
    
    /**
     * 检查titleId和ordering组合是否存在
     */
    boolean existsByTitleIdAndOrdering(String titleId, Integer ordering);
}