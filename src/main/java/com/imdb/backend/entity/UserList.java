package com.imdb.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_lists", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "name" })
})
@Data
@NoArgsConstructor
public class UserList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_system")
    private Boolean isSystem = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public UserList(User user, String name, Boolean isSystem) {
        this.user = user;
        this.name = name;
        this.isSystem = isSystem;
    }
}
