package com.imdb.backend.controller; // 控制器所在的包路径

import com.imdb.backend.entity.NameBasics; // 实体类
import com.imdb.backend.service.NameBasicsService; // 服务层接口
import org.springframework.http.ResponseEntity; // 构建HTTP响应
import org.springframework.web.bind.annotation.*; // REST控制器相关注解
import java.util.List; // List集合

// @Mapping表示该方法处理HTTP请求
@RestController // 该类为REST控制器，自动将返回值转换为JSON
@RequestMapping("/api/names") // 设置基础URL路径
public class NameBasicsController {
    private final NameBasicsService service; // 服务层接口注入

    // 构造函数依赖注入
    public NameBasicsController(NameBasicsService service) {
        this.service = service;
    }

    // 获取所有人员数据
    @GetMapping // 处理GET请求，路径为/api/names
    public List<NameBasics> all() {
        return service.listAll(); // 调用服务层获取所有数据
    }

    // 根据ID获取单个人员数据
    // var类似c++中的auto，自动推断类型
    @GetMapping("/{id}") // 处理GET请求，路径为/api/names/{id}
    public ResponseEntity<NameBasics> get(@PathVariable String id) { // 从URL中提取id参数
        var nb = service.findById(id); // 调用服务层根据ID查找
        // 如果找不到则返回404 Not Found，否则返回200 OK并携带数据
        return nb == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(nb);
    }

    // 根据名称搜索人员
    @GetMapping("/search") // 处理GET请求，路径为/api/names/search
    public List<NameBasics> search(@RequestParam("q") String q) { // 从查询参数中获取参数
        return service.searchByName(q); // 调用服务层搜索功能
    }

    // 创建新的人员数据
    @PostMapping // 处理POST请求，路径为/api/names
    public ResponseEntity<NameBasics> create(@RequestBody NameBasics nb) { // 从请求体中获取数据
        return ResponseEntity.ok(service.save(nb)); // 保存数据并返回200 OK
    }

    // 更新现有人员数据
    @PutMapping("/{id}") // 处理PUT请求，路径为/api/names/{id}
    public ResponseEntity<NameBasics> update(@PathVariable String id, @RequestBody NameBasics nb) {
        var exist = service.findById(id); // 检查数据是否存在
        if (exist == null)
            return ResponseEntity.notFound().build(); // 不存在返回404
        nb.setNconst(id); // 确保更新的是指定ID的数据
        return ResponseEntity.ok(service.save(nb)); // 保存更新并返回200 OK
    }

    // 删除人员数据
    @DeleteMapping("/{id}") // 处理DELETE请求，路径为/api/names/{id}
    public ResponseEntity<Void> delete(@PathVariable String id) {
        var exist = service.findById(id); // 检查数据是否存在
        if (exist == null)
            return ResponseEntity.notFound().build(); // 不存在返回404
        service.deleteById(id); // 删除数据
        return ResponseEntity.noContent().build(); // 返回204 No Content表示删除成功
    }
}