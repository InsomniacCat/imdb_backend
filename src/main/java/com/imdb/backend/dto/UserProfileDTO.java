package com.imdb.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String username;
    private String email;
    private String bio;
    private String avatarUrl;
}
