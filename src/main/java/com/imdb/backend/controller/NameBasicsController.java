package com.imdb.backend.controller;

import com.imdb.backend.entity.NameBasics;
import com.imdb.backend.service.NameBasicsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/names")
public class NameBasicsController {
    private final NameBasicsService service;
    public NameBasicsController(NameBasicsService service){ this.service = service; }

    @GetMapping
    public List<NameBasics> all(){ return service.listAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<NameBasics> get(@PathVariable String id){
        var nb = service.findById(id);
        return nb == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(nb);
    }

    @GetMapping("/search")
    public List<NameBasics> search(@RequestParam("q") String q){ return service.searchByName(q); }

    @PostMapping
    public ResponseEntity<NameBasics> create(@RequestBody NameBasics nb){
        return ResponseEntity.ok(service.save(nb));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NameBasics> update(@PathVariable String id, @RequestBody NameBasics nb){
        var exist = service.findById(id);
        if (exist == null) return ResponseEntity.notFound().build();
        nb.setNconst(id);
        return ResponseEntity.ok(service.save(nb));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        var exist = service.findById(id);
        if (exist == null) return ResponseEntity.notFound().build();
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}