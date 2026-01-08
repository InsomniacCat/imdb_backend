package com.imdb.backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 电影/剧集评分信息实体类
 * 用于映射数据库中的title_ratings表
 */
@Entity
@Table(name = "title_ratings")
public class TitleRatings {
    @Id
    @Column(name = "tconst", length = 20, nullable = false)
    private String tconst; // 与 TitleBasics 共享主键

    @Column(name = "averageRating")
    private Double averageRating;

    @Column(name = "numVotes")
    private Integer numVotes;

    // 建立与TitleBasics的关联，但由tconst字段管理外键/主键值，确保导入服务可以直接插入ID
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tconst", insertable = false, updatable = false)
    @JsonIgnore
    private TitleBasics titleBasics;

    // 无参构造函数
    public TitleRatings() {
    }

    // 全参构造函数
    public TitleRatings(String tconst, Double averageRating, Integer numVotes) {
        this.tconst = tconst;
        this.averageRating = averageRating;
        this.numVotes = numVotes;
    }

    // Getter和Setter方法
    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
    }

    public TitleBasics getTitleBasics() {
        return titleBasics;
    }

    public void setTitleBasics(TitleBasics titleBasics) {
        this.titleBasics = titleBasics;
    }
}