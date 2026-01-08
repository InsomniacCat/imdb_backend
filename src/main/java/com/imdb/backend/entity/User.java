package com.imdb.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role = "ROLE_USER";

    private String email;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
