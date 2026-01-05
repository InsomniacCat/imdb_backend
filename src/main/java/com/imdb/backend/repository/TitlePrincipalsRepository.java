package com.imdb.backend.repository;

import com.imdb.backend.entity.TitlePrincipals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TitlePrincipals实体的数据访问接口
 * 提供对title_principals表的基本CRUD操作和自定义查询方法
 */
@Repository
public interface TitlePrincipalsRepository extends JpaRepository<TitlePrincipals, com.imdb.backend.entity.TitlePrincipalsId> {
    
    /**
     * 根据tconst查找所有主要演职员
     * 注意：对于使用复合主键的实体，需要通过id.tconst访问主键中的字段
     */
    List<TitlePrincipals> findByIdTconst(String tconst);
    
    /**
     * 根据nconst查找所有参与作品
     */
    List<TitlePrincipals> findByNconst(String nconst);
    
    /**
     * 根据类别查找（如actor, actress, director等）
     */
    List<TitlePrincipals> findByCategory(String category);
    
    /**
     * 根据tconst和类别查找
     */
    List<TitlePrincipals> findByIdTconstAndCategory(String tconst, String category);
    
    /**
     * 根据角色名称查找（在characters字段中搜索）
     * 使用LIKE操作符在JSON格式的字符串中搜索角色名称
     */
    @Query("SELECT tp FROM TitlePrincipals tp WHERE tp.characters IS NOT NULL AND tp.characters LIKE %:character%")
    List<TitlePrincipals> findByCharacter(@Param("character") String character);
    
    /**
     * 检查tconst和ordering组合是否存在
     */
    boolean existsByIdTconstAndIdOrdering(String tconst, Integer ordering);
}