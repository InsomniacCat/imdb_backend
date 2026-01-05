package com.imdb.backend.repository;

import com.imdb.backend.entity.TitleCrew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TitleCrew实体的数据访问接口
 * 提供对title_crew表的基本CRUD操作和自定义查询方法
 */
@Repository
public interface TitleCrewRepository extends JpaRepository<TitleCrew, String> {
    
    /**
     * 根据导演ID查找作品（在directors列表中搜索）
     */
    @Query("SELECT tc FROM TitleCrew tc WHERE tc.directors IS NOT NULL AND CONCAT(',', tc.directors, ',') LIKE %:directorId%")
    List<TitleCrew> findByDirectorsContaining(@Param("directorId") String directorId);
    
    /**
     * 根据编剧ID查找作品（在writers列表中搜索）
     */
    @Query("SELECT tc FROM TitleCrew tc WHERE tc.writers IS NOT NULL AND CONCAT(',', tc.writers, ',') LIKE %:writerId%")
    List<TitleCrew> findByWritersContaining(@Param("writerId") String writerId);
    
    /**
     * 检查tconst是否存在
     */
    boolean existsByTconst(String tconst);
}