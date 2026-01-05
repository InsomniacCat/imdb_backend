package com.imdb.backend.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * TitlePrincipals实体类
 * 用于映射数据库中的title_principals表
 * 存储电影/剧集的主要演职人员信息
 */
@Entity
@Table(name = "title_principals")
public class TitlePrincipals implements Serializable {
    // 复合主键，包含tconst和ordering
    @EmbeddedId
    private TitlePrincipalsId id = new TitlePrincipalsId();

    // 人员ID
    @Column(name = "nconst", length = 255, nullable = false)
    private String nconst;

    // 类别（如actor, actress, director等）
    @Column(name = "category", length = 255)
    private String category;

    // 职位信息
    @Column(name = "job", length = 255)
    private String job;

    // 角色名称列表（JSON格式存储）
    @Convert(converter = StringListConverter.class) // 使用StringListConverter处理JSON字符串
    @Column(name = "characters", columnDefinition = "TEXT")
    private List<String> characters;

    // 无参构造函数（JPA要求）
    public TitlePrincipals() {
    }

    // 全参构造函数
    public TitlePrincipals(TitlePrincipalsId id, String nconst, String category, String job, List<String> characters) {
        this.id = id;
        this.nconst = nconst;
        this.category = category;
        this.job = job;
        this.characters = characters;
    }

    // getter和setter方法
    public TitlePrincipalsId getId() {
        return id;
    }

    public void setId(TitlePrincipalsId id) {
        this.id = id;
    }

    public String getNconst() {
        return nconst;
    }

    public void setNconst(String nconst) {
        this.nconst = nconst;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public List<String> getCharacters() {
        return characters;
    }

    public void setCharacters(List<String> characters) {
        this.characters = characters;
    }
}