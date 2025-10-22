// filepath: d:\Work\SpringBoot\backend\src\main\java\com\imdb\backend\service\NameBasicsService.java
package com.imdb.backend.service;

import com.imdb.backend.entity.NameBasics;
import com.imdb.backend.repository.NameBasicsRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NameBasicsService {
    private final NameBasicsRepository repo;
    public NameBasicsService(NameBasicsRepository repo){ this.repo = repo; }

    public List<NameBasics> listAll(){ return repo.findAll(); }
    public NameBasics findById(String id){ return repo.findById(id).orElse(null); }
    public NameBasics save(NameBasics nb){ return repo.save(nb); }
    public List<NameBasics> searchByName(String q){ return repo.findByPrimaryNameContainingIgnoreCase(q); }
    public void deleteById(String id){ repo.deleteById(id); }
}