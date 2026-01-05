package com.imdb.backend.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * 电影/剧集基本信息实体类
 * 用于映射数据库中的title_basics表
 */
@Entity
@Table(name = "title_basics")
public class TitleBasics {
    @Id
    @Column(name = "tconst", length = 20, nullable = false)
    private String tconst;

    @Column(name = "title_type", length = 50)
    private String titleType;
    
    @Column(name = "primary_title", columnDefinition = "TEXT")
    private String primaryTitle;
    
    @Column(name = "original_title", columnDefinition = "TEXT")
    private String originalTitle;
    
    @Column(name = "is_adult")
    private Boolean isAdult;
    
    @Column(name = "start_year")
    private Integer startYear;
    
    @Column(name = "end_year")
    private Integer endYear;
    
    @Column(name = "runtime_minutes")
    private Integer runtimeMinutes;

    @Convert(converter = StringListConverter.class)
    @Column(name = "genres", columnDefinition = "TEXT")
    private List<String> genres;

    // 无参构造函数
    public TitleBasics() {}

    // 全参构造函数
    public TitleBasics(String tconst, String titleType, String primaryTitle, String originalTitle, 
                       Boolean isAdult, Integer startYear, Integer endYear, Integer runtimeMinutes, List<String> genres) {
        this.tconst = tconst;
        this.titleType = titleType;
        this.primaryTitle = primaryTitle;
        this.originalTitle = originalTitle;
        this.isAdult = isAdult;
        this.startYear = startYear;
        this.endYear = endYear;
        this.runtimeMinutes = runtimeMinutes;
        this.genres = genres;
    }

    // Getter和Setter方法
    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
    }

    public String getTitleType() {
        return titleType;
    }

    public void setTitleType(String titleType) {
        this.titleType = titleType;
    }

    public String getPrimaryTitle() {
        return primaryTitle;
    }

    public void setPrimaryTitle(String primaryTitle) {
        this.primaryTitle = primaryTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public Boolean getIsAdult() {
        return isAdult;
    }

    public void setIsAdult(Boolean isAdult) {
        this.isAdult = isAdult;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public Integer getRuntimeMinutes() {
        return runtimeMinutes;
    }

    public void setRuntimeMinutes(Integer runtimeMinutes) {
        this.runtimeMinutes = runtimeMinutes;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "TitleBasics{" +
                "tconst='" + tconst + '\'' +
                ", titleType='" + titleType + '\'' +
                ", primaryTitle='" + primaryTitle + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", isAdult=" + isAdult +
                ", startYear=" + startYear +
                ", endYear=" + endYear +
                ", runtimeMinutes=" + runtimeMinutes +
                ", genres=" + genres +
                '}';
    }
}