package com.imdb.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorites", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "item_id" })
})
@Data
@NoArgsConstructor
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    private UserList list;

    @Column(name = "item_id", nullable = false)
    private String itemId; // tconst or nconst

    @Column(name = "item_type", nullable = false)
    private String itemType; // TITLE or NAME

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Favorite(User user, String itemId, String itemType) {
        this.user = user;
        this.itemId = itemId;
        this.itemType = itemType;
    }
}
