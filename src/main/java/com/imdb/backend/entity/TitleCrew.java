package com.imdb.backend.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * 电影/剧集工作人员信息实体类
 * 用于映射数据库中的title_crew表
 */
@Entity
@Table(name = "title_crew")
public class TitleCrew {
    @Id
    @Column(name = "tconst", length = 20, nullable = false)
    private String tconst;

    @Convert(converter = StringListConverter.class)
    @Column(name = "directors", columnDefinition = "TEXT")
    private List<String> directors; // 存储 nmxxxx ID

    @Convert(converter = StringListConverter.class)
    @Column(name = "writers", columnDefinition = "TEXT")
    private List<String> writers; // 存储 nmxxxx ID

    // 无参构造函数
    public TitleCrew() {}

    // 全参构造函数
    public TitleCrew(String tconst, List<String> directors, List<String> writers) {
        this.tconst = tconst;
        this.directors = directors;
        this.writers = writers;
    }

    // Getter和Setter方法
    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }

    public List<String> getWriters() {
        return writers;
    }

    public void setWriters(List<String> writers) {
        this.writers = writers;
    }

    @Override
    public String toString() {
        return "TitleCrew{" +
                "tconst='" + tconst + '\'' +
                ", directors=" + directors +
                ", writers=" + writers +
                '}';
    }
}