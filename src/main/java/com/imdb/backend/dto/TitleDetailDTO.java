package com.imdb.backend.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TitleDetailDTO {
    private String tconst;
    private String primaryTitle;
    private String originalTitle;
    private Boolean isAdult;
    private Integer startYear;
    private Integer endYear;
    private Integer runtimeMinutes;
    private List<String> genres;

    // Ratings
    private Double averageRating;
    private Integer numVotes;

    // Cast & Crew
    private List<CastMemberDTO> cast;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CastMemberDTO {
        private String nconst;
        private String primaryName;
        private String category; // actor, director, etc.
        private String job; // specific job description
        private String characters; // JSON string of characters played
    }
}
