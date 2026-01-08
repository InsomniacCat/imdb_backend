package com.imdb.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * TitlePrincipals实体的复合主键类
 * 由tconst和ordering组成
 */
@Embeddable
public class TitlePrincipalsId implements Serializable {

    @Column(name = "tconst", length = 20, nullable = false)
    private String tconst;

    @Column(name = "ordering", nullable = false)
    private Integer ordering;

    // 无参构造函数
    public TitlePrincipalsId() {
    }

    // 全参构造函数
    public TitlePrincipalsId(String tconst, Integer ordering) {
        this.tconst = tconst;
        this.ordering = ordering;
    }

    // Getter和Setter方法
    public String getTconst() {
        return tconst;
    }

    public void setTconst(String tconst) {
        this.tconst = tconst;
    }

    public Integer getOrdering() {
        return ordering;
    }

    public void setOrdering(Integer ordering) {
        this.ordering = ordering;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TitlePrincipalsId that = (TitlePrincipalsId) o;
        return Objects.equals(tconst, that.tconst) && Objects.equals(ordering, that.ordering);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tconst, ordering);
    }

    @Override
    public String toString() {
        return "TitlePrincipalsId{" +
                "tconst='" + tconst + '\'' +
                ", ordering=" + ordering +
                '}';
    }
}