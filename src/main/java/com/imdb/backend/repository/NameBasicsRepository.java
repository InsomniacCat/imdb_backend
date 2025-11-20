package com.imdb.backend.repository;

import com.imdb.backend.entity.NameBasics;// 导入实体类，作为仓库操作的目标对象
import org.springframework.data.jpa.repository.JpaRepository;// 导入Spring Data JPA核心接口，提供基本CRUD操作
import java.util.List;// 导入List集合，用于返回查询结果

// NameBasicsRepository接口：继承JpaRepository，提供NameBasics实体的数据库访问功能
public interface NameBasicsRepository extends JpaRepository<NameBasics, String> {
    // Spring Data JPA会根据方法名自动生成SQL查询
    List<NameBasics> findByPrimaryNameContainingIgnoreCase(String namePart);
}