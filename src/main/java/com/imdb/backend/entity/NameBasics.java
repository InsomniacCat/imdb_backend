package com.imdb.backend.entity; 

import jakarta.persistence.*; // JPA相关注解
import java.io.Serializable; // Serializable接口
import java.util.List; // List集合接口

/**
 * 人员基本信息实体类
 * 用于映射数据库中的name_basics表
 */
@Entity // 标识此类为JPA实体类，对应数据库中的表
@Table(name = "name_basics") // 指定实体类映射的表名
public class NameBasics implements Serializable { // 标注Serializable接口，支持序列化
    //序列化是将对象转换为字节流的过程，对象需要被序列化为JSON格式

    @Id // 主键字段
    @Column(name = "nconst", length = 255, nullable = false) // 指定映射到表中的列名、长度和非空约束
    private String nconst; // 人员的唯一标识符

    @Column(name = "primaryName", columnDefinition = "TEXT", nullable = false) // 指定列定义为TEXT类型且非空
    private String primaryName; // 人员的姓名

    @Column(name = "birthYear") // 映射到birthYear列
    private Integer birthYear; // 出生年份

    @Column(name = "deathYear") // 映射到deathYear列
    private Integer deathYear; // 死亡年份

    @Convert(converter = StringListConverter.class) // 使用自定义转换器将List<String>转换为数据库可存储格式
    @Column(name = "primaryProfession", columnDefinition = "TEXT") // 定义为TEXT类型存储多个职业
    private List<String> primaryProfession; // 主要职业列表

    @Convert(converter = StringListConverter.class) // 使用自定义转换器处理
    @Column(name = "knownForTitles", columnDefinition = "TEXT") // 定义为TEXT类型存储多个作品ID
    private List<String> knownForTitles; // 知名作品ID列表

    /**
     * 无参构造函数
     * JPA框架通过Java反射机制创建对象时，默认需要一个无参构造函数
     */
    public NameBasics() {
    }

    /**
     * 全参构造函数
     * 提供便捷的方式来创建并初始化所有属性的对象
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

    /**
     * 获取人员唯一标识符
     */
    public String getNconst() {
        return nconst;
    }

    /**
     * 设置人员唯一标识符
     */
    public void setNconst(String nconst) {
        this.nconst = nconst;
    }

    /**
     * 获取人员主要姓名
     */
    public String getPrimaryName() {
        return primaryName;
    }

    /**
     * 设置人员主要姓名
     */
    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    /**
     * 获取出生年份
     */
    public Integer getBirthYear() {
        return birthYear;
    }

    /**
     * 设置出生年份
     */
    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    /**
     * 获取死亡年份
     */
    public Integer getDeathYear() {
        return deathYear;
    }

    /**
     * 设置死亡年份
     */
    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    /**
     * 获取主要职业列表
     */
    public List<String> getPrimaryProfession() {
        return primaryProfession;
    }

    /**
     * 设置主要职业列表
     */
    public void setPrimaryProfession(List<String> primaryProfession) {
        this.primaryProfession = primaryProfession;
    }

    /**
     * 获取知名作品ID列表
     */
    public List<String> getKnownForTitles() {
        return knownForTitles;
    }

    /**
     * 设置知名作品ID列表
     */
    public void setKnownForTitles(List<String> knownForTitles) {
        this.knownForTitles = knownForTitles;
    }
}