package com.imdb.backend.entity;

import jakarta.persistence.*;

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

    // 修复PostgreSQL中浮点数类型的precision和scale问题
    @Column(name = "average_rating")
    private Double averageRating;
    
    @Column(name = "num_votes")
    private Integer numVotes;

    // 无参构造函数
    public TitleRatings() {}

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

    @Override
    public String toString() {
        return "TitleRatings{" +
                "tconst='" + tconst + '\'' +
                ", averageRating=" + averageRating +
                ", numVotes=" + numVotes +
                '}';
    }
}