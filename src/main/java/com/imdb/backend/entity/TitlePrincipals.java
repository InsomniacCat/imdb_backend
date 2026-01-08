package com.imdb.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
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

    // 建立与TitleBasics的关联
    // 使用 @MapsId("tconst") 可能会导致问题，因为这是复合主键的一部分
    // 所以这里使用 @JoinColumn 并设置 insertable=false, updatable=false，让 id.tconst 负责实际的值
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tconst", insertable = false, updatable = false)
    @JsonIgnore
    private TitleBasics titleBasics;

    // 人员ID
    @Column(name = "nconst", length = 255, nullable = false)
    private String nconst;

    // 建立与 NameBasics 的关联 (Optional, for easy navigation)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nconst", insertable = false, updatable = false)
    @JsonIgnore
    private NameBasics nameBasics;

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

    public TitleBasics getTitleBasics() {
        return titleBasics;
    }

    public void setTitleBasics(TitleBasics titleBasics) {
        this.titleBasics = titleBasics;
    }

    public NameBasics getNameBasics() {
        return nameBasics;
    }

    public void setNameBasics(NameBasics nameBasics) {
        this.nameBasics = nameBasics;
    }
}