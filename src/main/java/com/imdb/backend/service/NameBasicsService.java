package com.imdb.backend.service;

import com.imdb.backend.entity.NameBasics;
import com.imdb.backend.repository.NameBasicsRepository;
import org.springframework.stereotype.Service;
import java.util.List;

// 将该类标识为服务组件
@Service
public class NameBasicsService {
    // 注入数据访问层的仓库对象
    // final 确保它在构造后不会被重新赋值
    private final NameBasicsRepository repo;
    
    // 构造函数注入仓库依赖
    public NameBasicsService(NameBasicsRepository repo){ 
        this.repo = repo; 
    }

    // 获取所有NameBasics实体的列表
    public List<NameBasics> listAll(){ 
        return repo.findAll(); 
    }
    
    // 根据ID查找特定的NameBasics实体
    public NameBasics findById(String id){ 
        return repo.findById(id).orElse(null); 
    }
    
    // 保存或更新NameBasics实体
    public NameBasics save(NameBasics nb){ 
        return repo.save(nb); 
    }
    
    // 根据姓名进行模糊搜索
    public List<NameBasics> searchByName(String q){ 
        return repo.findByPrimaryNameContainingIgnoreCase(q); 
    }
    
    // 根据ID删除NameBasics实体
    public void deleteById(String id){ 
        repo.deleteById(id); 
    }
}