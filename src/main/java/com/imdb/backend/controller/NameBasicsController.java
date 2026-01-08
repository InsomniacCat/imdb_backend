package com.imdb.backend.controller;

import com.imdb.backend.dto.NameCareerDTO;
import com.imdb.backend.entity.NameBasics;
import com.imdb.backend.service.NameBasicsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*") // 允许跨域
@RestController
@RequestMapping("/api/names")
public class NameBasicsController {
    private final NameBasicsService service;

    public NameBasicsController(NameBasicsService service) {
        this.service = service;
    }

    // 获取所有人员数据 (分页)
    @GetMapping
    public ResponseEntity<Map<String, Object>> all(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nconst") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<NameBasics> pageResult = service.listAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("names", pageResult.getContent());
        response.put("currentPage", pageResult.getNumber());
        response.put("totalItems", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // 根据ID获取单个人员数据
    @GetMapping("/{id}")
    public ResponseEntity<NameBasics> get(@PathVariable String id) {
        var nb = service.findById(id);
        return nb == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(nb);
    }

    // 根据名称搜索人员
    @GetMapping("/search")
    public List<NameBasics> search(@RequestParam("q") String q) {
        return service.searchByName(q);
    }

    // 获取影人生涯数据分析 (图表数据)
    @GetMapping("/{id}/career")
    public ResponseEntity<NameCareerDTO> getCareer(@PathVariable String id) {
        var analytics = service.getCareerAnalytics(id);
        return analytics == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(analytics);
    }

    // 创建新的人员数据
    @PostMapping
    public ResponseEntity<NameBasics> create(@RequestBody NameBasics nb) {
        return ResponseEntity.ok(service.save(nb));
    }

    // 更新现有人员数据
    @PutMapping("/{id}")
    public ResponseEntity<NameBasics> update(@PathVariable String id, @RequestBody NameBasics nb) {
        var exist = service.findById(id);
        if (exist == null)
            return ResponseEntity.notFound().build();
        nb.setNconst(id);
        return ResponseEntity.ok(service.save(nb));
    }

    // 删除人员数据
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        var exist = service.findById(id);
        if (exist == null)
            return ResponseEntity.notFound().build();
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}