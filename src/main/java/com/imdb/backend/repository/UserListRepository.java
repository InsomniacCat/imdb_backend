package com.imdb.backend.repository;

import com.imdb.backend.entity.UserList;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserListRepository extends JpaRepository<UserList, Long> {
    List<UserList> findByUserIdOrderByCreatedAtAsc(Long userId);

    Optional<UserList> findByUserIdAndName(Long userId, String name);
}
