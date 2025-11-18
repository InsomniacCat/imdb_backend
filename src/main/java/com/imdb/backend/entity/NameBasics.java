package com.imdb.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "name_basics")
public class NameBasics implements Serializable {

    @Id
    @Column(name = "nconst", length = 255, nullable = false)
    private String nconst;

    @Column(name = "primaryName", columnDefinition = "TEXT", nullable = false)
    private String primaryName;

    @Column(name = "birthYear")
    private Integer birthYear;

    @Column(name = "deathYear")
    private Integer deathYear;

    @Convert(converter = StringListConverter.class)
    @Column(name = "primaryProfession", columnDefinition = "TEXT")
    private List<String> primaryProfession;

    @Convert(converter = StringListConverter.class)
    @Column(name = "knownForTitles", columnDefinition = "TEXT")
    private List<String> knownForTitles;
    
    /**
     * 无参构造函数
     * 必须提供以满足JPA框架的要求
     * JPA在从数据库加载数据时会使用此构造函数创建对象实例
     */
    public NameBasics() {}
    
    /**
     * 全参构造函数
     * 提供便捷的方式来创建并初始化所有属性的对象
     * 
     * @param nconst 人员的唯一标识符
     * @param primaryName 人员的主要姓名
     * @param birthYear 出生年份
     * @param deathYear 死亡年份
     * @param primaryProfession 主要职业列表
     * @param knownForTitles 知名作品列表
     */
    public NameBasics(String nconst, String primaryName, Integer birthYear, Integer deathYear, 
                      List<String> primaryProfession, List<String> knownForTitles) {
        this.nconst = nconst;
        this.primaryName = primaryName;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
        this.primaryProfession = primaryProfession;
        this.knownForTitles = knownForTitles;
    }
    
    public String getNconst() {
        return nconst;
    }
    public void setNconst(String nconst) {
        this.nconst = nconst;
    }

    public String getPrimaryName() {
        return primaryName;
    }
    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    public Integer getBirthYear() {
        return birthYear;
    }
    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }
    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public List<String> getPrimaryProfession() {
        return primaryProfession;
    }
    public void setPrimaryProfession(List<String> primaryProfession) {
        this.primaryProfession = primaryProfession;
    }

    public List<String> getKnownForTitles() {
        return knownForTitles;
    }
    public void setKnownForTitles(List<String> knownForTitles) {
        this.knownForTitles = knownForTitles;
    }
}