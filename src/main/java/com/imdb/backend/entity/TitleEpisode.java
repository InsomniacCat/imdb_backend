package com.imdb.backend.entity;

import jakarta.persistence.*;

/**
 * 剧集分集信息实体类
 * 用于映射数据库中的title_episode表
 */
@Entity
@Table(name = "title_episode")
public class TitleEpisode {
    @Id
    @Column(name = "tconst", length = 20, nullable = false)
    private String tconst;

    @Column(name = "parentTconst", length = 20)
    private String parentTconst;

    @Column(name = "seasonNumber")
    private Integer seasonNumber;

    @Column(name = "episodeNumber")
    private Integer episodeNumber;

    // 无参构造函数
    public TitleEpisode() {
    }

    // 全参构造函数
    public TitleEpisode(String tconst, String parentTconst, Integer seasonNumber, Integer episodeNumber) {
        this.tconst = tconst;
        this.parentTconst = parentTconst;
        this.seasonNumber = seasonNumber;
        this.episodeNumber = episodeNumber;
    }

    // Getter和Setter方法
    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
    }

    public String getParentTconst() {
        return parentTconst;
    }

    public void setParentTconst(String parentTconst) {
        this.parentTconst = parentTconst;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    @Override
    public String toString() {
        return "TitleEpisode{" +
                "tconst='" + tconst + '\'' +
                ", parentTconst='" + parentTconst + '\'' +
                ", seasonNumber=" + seasonNumber +
                ", episodeNumber=" + episodeNumber +
                '}';
    }
}