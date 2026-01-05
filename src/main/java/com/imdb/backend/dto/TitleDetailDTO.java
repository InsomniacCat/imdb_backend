package com.imdb.backend.dto;

import com.imdb.backend.entity.TitleBasics;
import com.imdb.backend.entity.TitleCrew;
import com.imdb.backend.entity.TitleRatings;

import java.util.List;

public class TitleDetailDTO {
    private TitleBasics titleBasics;
    private TitleRatings ratings;
    private TitleCrew crew;
    private List<CastMemberDTO> cast;

    public TitleDetailDTO() {
    }

    public TitleDetailDTO(TitleBasics titleBasics, TitleRatings ratings, TitleCrew crew, List<CastMemberDTO> cast) {
        this.titleBasics = titleBasics;
        this.ratings = ratings;
        this.crew = crew;
        this.cast = cast;
    }

    public TitleBasics getTitleBasics() {
        return titleBasics;
    }

    public void setTitleBasics(TitleBasics titleBasics) {
        this.titleBasics = titleBasics;
    }

    public TitleRatings getRatings() {
        return ratings;
    }

    public void setRatings(TitleRatings ratings) {
        this.ratings = ratings;
    }

    public TitleCrew getCrew() {
        return crew;
    }

    public void setCrew(TitleCrew crew) {
        this.crew = crew;
    }

    public List<CastMemberDTO> getCast() {
        return cast;
    }

    public void setCast(List<CastMemberDTO> cast) {
        this.cast = cast;
    }
}
