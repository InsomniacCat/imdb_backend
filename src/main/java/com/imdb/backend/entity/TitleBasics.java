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

    @Column(name = "titleType", length = 50)
    private String titleType;

    @Column(name = "primaryTitle", columnDefinition = "TEXT")
    private String primaryTitle;

    // original_title can be useful but for simplification, we keep it as just a
    // column without heavy logic
    @Column(name = "originalTitle", columnDefinition = "TEXT")
    private String originalTitle;

    @Column(name = "isAdult")
    private Boolean isAdult;

    @Column(name = "startYear")
    private Integer startYear;

    @Column(name = "endYear")
    private Integer endYear;

    @Column(name = "runtimeMinutes")
    private Integer runtimeMinutes;

    @Convert(converter = StringListConverter.class)
    @Column(name = "genres", columnDefinition = "TEXT")
    private List<String> genres;

    // 建立一对一关系，级联保存
    @OneToOne(mappedBy = "titleBasics", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TitleRatings ratings;

    // 无参构造函数
    public TitleBasics() {
    }

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

    public TitleRatings getRatings() {
        return ratings;
    }

    public void setRatings(TitleRatings ratings) {
        this.ratings = ratings;
        if (ratings != null) {
            ratings.setTitleBasics(this);
        }
    }
}