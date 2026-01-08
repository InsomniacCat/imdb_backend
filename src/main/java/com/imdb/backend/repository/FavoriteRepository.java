package com.imdb.backend.repository;

import com.imdb.backend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Favorite> findByUserIdAndItemType(Long userId, String itemType);

    Optional<Favorite> findByUserIdAndItemId(Long userId, String itemId);

    // New methods for Lists
    List<Favorite> findByListIdOrderByCreatedAtDesc(Long listId);

    void deleteByListIdAndItemId(Long listId, String itemId);

    boolean existsByListIdAndItemId(Long listId, String itemId);

    boolean existsByUserIdAndItemId(Long userId, String itemId);

    void deleteByUserIdAndItemId(Long userId, String itemId);
}
