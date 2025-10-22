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
    private Short birthYear;

    @Column(name = "deathYear")
    private Short deathYear;

    @Convert(converter = StringListConverter.class)
    @Column(name = "primaryProfession", columnDefinition = "TEXT")
    private List<String> primaryProfession;

    @Convert(converter = StringListConverter.class)
    @Column(name = "knownForTitles", columnDefinition = "TEXT")
    private List<String> knownForTitles;
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

    public Short getBirthYear() {
        return birthYear;
    }
    public void setBirthYear(Short birthYear) {
        this.birthYear = birthYear;
    }

    public Short getDeathYear() {
        return deathYear;
    }
    public void setDeathYear(Short deathYear) {
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