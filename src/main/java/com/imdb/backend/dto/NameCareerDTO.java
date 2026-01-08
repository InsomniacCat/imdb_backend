package com.imdb.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameCareerDTO {
    private BasicInfo basicInfo;
    private List<YearlyRating> yearlyRatings;
    private List<GenreStat> genreStats;
    private List<WorkSummary> famousWorks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicInfo {
        private String nconst;
        private String primaryName;
        private Integer birthYear;
        private Integer deathYear;
        private String primaryProfession;
        private List<String> knownForTitles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YearlyRating {
        private Integer year;
        private Double averageRating; // 该年平均分
        private Integer movieCount; // 该年作品数
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenreStat {
        private String genre;
        private Integer count;
        private Double avgRating;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkSummary {
        private String tconst;
        private String primaryTitle;
        private Integer startYear;
        private Double averageRating;
    }
}
