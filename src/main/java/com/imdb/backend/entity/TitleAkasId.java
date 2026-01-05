package com.imdb.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * TitleAkas实体的复合主键类
 * 由titleId和ordering组成
 */
@Embeddable
public class TitleAkasId implements Serializable {
    
    @Column(name = "title_id")
    private String titleId;
    
    @Column(name = "ordering")
    private Integer ordering;
    
    public TitleAkasId() {}
    
    public TitleAkasId(String titleId, Integer ordering) {
        this.titleId = titleId;
        this.ordering = ordering;
    }
    
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TitleAkasId that = (TitleAkasId) o;
        return Objects.equals(titleId, that.titleId) && Objects.equals(ordering, that.ordering);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(titleId, ordering);
    }
}