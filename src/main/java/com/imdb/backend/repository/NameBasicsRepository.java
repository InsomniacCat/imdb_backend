package com.imdb.backend.repository;

import com.imdb.backend.entity.NameBasics;// 导入实体类
import org.springframework.data.jpa.repository.JpaRepository;// 导入JPA，获得完整的CRUD操作
import java.util.List;// 导入List

// 提供NameBasics实体的数据库访问功能
import org.springframework.data.domain.Pageable;

public interface NameBasicsRepository extends JpaRepository<NameBasics, String> {
    // 方法名查询派生，JPA会根据方法名自动生成SQL查询
    // 旧方法（不推荐使用，可能返回大量数据）
    List<NameBasics> findByPrimaryNameContainingIgnoreCase(String namePart);

    // 新增：带分页限制的搜索方法
    List<NameBasics> findByPrimaryNameContainingIgnoreCase(String namePart, Pageable pageable);
}