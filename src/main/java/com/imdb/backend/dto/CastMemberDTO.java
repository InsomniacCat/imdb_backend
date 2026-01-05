package com.imdb.backend.dto;

import java.util.List;

public class CastMemberDTO {
    private String nconst;
    private String primaryName;
    private String category;
    private String job;
    private List<String> characters;

    // Constructors
    public CastMemberDTO() {
    }

    public CastMemberDTO(String nconst, String primaryName, String category, String job, List<String> characters) {
        this.nconst = nconst;
        this.primaryName = primaryName;
        this.category = category;
        this.job = job;
        this.characters = characters;
    }

    // Getters and Setters
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public List<String> getCharacters() {
        return characters;
    }

    public void setCharacters(List<String> characters) {
        this.characters = characters;
    }
}
