// filepath: d:\Work\SpringBoot\backend\src\main\java\com\imdb\backend\repository\NameBasicsRepository.java
package com.imdb.backend.repository;

import com.imdb.backend.entity.NameBasics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NameBasicsRepository extends JpaRepository<NameBasics, String> {
    List<NameBasics> findByPrimaryNameContainingIgnoreCase(String namePart);
}