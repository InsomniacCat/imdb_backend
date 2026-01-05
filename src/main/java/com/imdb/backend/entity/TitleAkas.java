package com.imdb.backend.entity;

import jakarta.persistence.*;

/**
 * 电影/剧集别名信息实体类
 * 用于映射数据库中的title_akas表
 */
@Entity
@Table(name = "title_akas")
@IdClass(TitleAkasId.class) // 使用外部定义的复合主键类
public class TitleAkas {
    @Id
    @Column(name = "title_id", length = 20)
    private String titleId;

    @Id
    @Column(name = "ordering")
    private Integer ordering;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;
    
    @Column(name = "region", length = 10)
    private String region;
    
    @Column(name = "language", length = 10)
    private String language;
    
    @Column(name = "types", columnDefinition = "TEXT")
    private String types;
    
    @Column(name = "attributes", columnDefinition = "TEXT")
    private String attributes;
    
    @Column(name = "is_original_title")
    private Boolean isOriginalTitle;

    // 无参构造函数
    public TitleAkas() {}

    // 全参构造函数
    public TitleAkas(String titleId, Integer ordering, String title, String region, 
                     String language, String types, String attributes, Boolean isOriginalTitle) {
        this.titleId = titleId;
        this.ordering = ordering;
        this.title = title;
        this.region = region;
        this.language = language;
        this.types = types;
        this.attributes = attributes;
        this.isOriginalTitle = isOriginalTitle;
    }

    // Getters and Setters
    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public Integer getOrdering() {
        return ordering;
    }

    public void setOrdering(Integer ordering) {
        this.ordering = ordering;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public Boolean getIsOriginalTitle() {
        return isOriginalTitle;
    }

    public void setIsOriginalTitle(Boolean isOriginalTitle) {
        this.isOriginalTitle = isOriginalTitle;
    }

    @Override
    public String toString() {
        return "TitleAkas{" +
                "titleId='" + titleId + '\'' +
                ", ordering=" + ordering +
                ", title='" + title + '\'' +
                ", region='" + region + '\'' +
                ", language='" + language + '\'' +
                ", types='" + types + '\'' +
                ", attributes='" + attributes + '\'' +
                ", isOriginalTitle=" + isOriginalTitle +
                '}';
    }
}