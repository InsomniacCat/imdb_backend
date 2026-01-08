package com.imdb.backend.dto;

import lombok.Data;

@Data
public class FavoriteDTO {
    private String itemId;
    private String itemType;
    private String title; // item name/title (optional for display)
    private String poster; // optional
    // We will populate details when fetching
}
