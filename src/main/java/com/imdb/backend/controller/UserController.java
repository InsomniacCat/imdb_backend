package com.imdb.backend.controller;

import com.imdb.backend.dto.FavoriteDTO;
import com.imdb.backend.dto.UserProfileDTO;
import com.imdb.backend.entity.*;
import com.imdb.backend.repository.*;
import com.imdb.backend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepo;
    private final FavoriteRepository favRepo;
    private final UserListRepository listRepo;
    private final NameBasicsRepository nameRepo;
    private final TitleBasicsRepository titleRepo;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepo, FavoriteRepository favRepo, UserListRepository listRepo,
            NameBasicsRepository nameRepo, TitleBasicsRepository titleRepo,
            JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.favRepo = favRepo;
        this.listRepo = listRepo;
        this.nameRepo = nameRepo;
        this.titleRepo = titleRepo;
        this.jwtUtil = jwtUtil;
    }

    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // --- Profile ---

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestHeader("Authorization") String token) {
        User user = getUserFromToken(token);
        return ResponseEntity.ok(new UserProfileDTO(
                user.getUsername(), user.getEmail(), user.getBio(), user.getAvatarUrl()));
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestHeader("Authorization") String token,
            @RequestBody UserProfileDTO dto) {
        User user = getUserFromToken(token);
        if (dto.getEmail() != null)
            user.setEmail(dto.getEmail());
        if (dto.getBio() != null)
            user.setBio(dto.getBio());
        if (dto.getAvatarUrl() != null)
            user.setAvatarUrl(dto.getAvatarUrl());
        userRepo.save(user);
        return ResponseEntity.ok("Profile updated");
    }

    // --- Lists ---

    @GetMapping("/lists")
    public ResponseEntity<List<UserList>> getLists(@RequestHeader("Authorization") String token) {
        User user = getUserFromToken(token);
        List<UserList> lists = listRepo.findByUserIdOrderByCreatedAtAsc(user.getId());

        // Auto-create Watchlist if empty
        if (lists.isEmpty()) {
            UserList watchlist = new UserList(user, "Watchlist", true);
            listRepo.save(watchlist);
            lists.add(watchlist);
        }
        return ResponseEntity.ok(lists);
    }

    @PostMapping("/lists")
    public ResponseEntity<?> createList(@RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> payload) {
        User user = getUserFromToken(token);
        String name = payload.get("name");
        if (name == null || name.trim().isEmpty())
            return ResponseEntity.badRequest().body("Name required");

        if (listRepo.findByUserIdAndName(user.getId(), name).isPresent()) {
            return ResponseEntity.badRequest().body("List already exists");
        }

        UserList list = new UserList(user, name, false);
        return ResponseEntity.ok(listRepo.save(list));
    }

    @DeleteMapping("/lists/{id}")
    @Transactional
    public ResponseEntity<?> deleteList(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        User user = getUserFromToken(token);
        UserList list = listRepo.findById(id).orElseThrow(() -> new RuntimeException("List not found"));

        if (!list.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(403).build();
        if (list.getIsSystem())
            return ResponseEntity.badRequest().body("Cannot delete system list");

        listRepo.delete(list);
        return ResponseEntity.ok().build();
    }

    // --- Favorites ---

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteDTO>> getFavorites(@RequestHeader("Authorization") String token,
            @RequestParam(required = false) Long listId) {
        User user = getUserFromToken(token);
        List<Favorite> favs;

        try {
            if (listId != null) {
                // Securely verify list ownership first
                UserList list = listRepo.findById(listId)
                        .orElseThrow(() -> new RuntimeException("List not found"));

                if (!list.getUser().getId().equals(user.getId())) {
                    // Fail silently or throw error? Let's return empty to be safe or 403.
                    // Returning 403 might break frontend if it doesn't handle it well,
                    // but it's the correct semantics.
                    throw new RuntimeException("Unauthorized access to list");
                }

                favs = favRepo.findByListIdOrderByCreatedAtDesc(listId);
            } else {
                // Default: Get Watchlist
                UserList watchList = listRepo.findByUserIdAndName(user.getId(), "Watchlist")
                        .orElseGet(() -> {
                            UserList newList = new UserList(user, "Watchlist", true);
                            return listRepo.save(newList);
                        });
                favs = favRepo.findByListIdOrderByCreatedAtDesc(watchList.getId());
            }

            List<FavoriteDTO> dtos = new ArrayList<>();
            for (Favorite f : favs) {
                FavoriteDTO dto = new FavoriteDTO();
                dto.setItemId(f.getItemId());
                dto.setItemType(f.getItemType());

                if ("TITLE".equals(f.getItemType())) {
                    Optional<TitleBasics> t = titleRepo.findById(f.getItemId());
                    dto.setTitle(t.map(TitleBasics::getPrimaryTitle).orElse("Unknown Title"));
                } else {
                    Optional<NameBasics> n = nameRepo.findById(f.getItemId());
                    dto.setTitle(n.map(NameBasics::getPrimaryName).orElse("Unknown Name"));
                }
                dtos.add(dto);
            }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            e.printStackTrace(); // Start logging errors!
            // Return empty list on error to prevent frontend crash loop
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/favorites")
    public ResponseEntity<String> addFavorite(@RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> payload) {
        User user = getUserFromToken(token);
        String itemId = (String) payload.get("itemId");
        String itemType = (String) payload.get("itemType");
        Long listId = payload.get("listId") != null ? Long.valueOf(payload.get("listId").toString()) : null;

        UserList list;
        if (listId != null) {
            list = listRepo.findById(listId).orElseThrow(() -> new RuntimeException("List not found"));
            if (!list.getUser().getId().equals(user.getId()))
                return ResponseEntity.status(403).body("Not your list");
        } else {
            // Default to Watchlist
            list = listRepo.findByUserIdAndName(user.getId(), "Watchlist").orElseGet(() -> {
                UserList l = new UserList(user, "Watchlist", true);
                return listRepo.save(l);
            });
        }

        // Check if already in THIS list
        if (favRepo.existsByListIdAndItemId(list.getId(), itemId)) {
            return ResponseEntity.ok("Already in list");
        }

        try {
            Favorite fav = new Favorite();
            fav.setUser(user); // Keep user ref
            fav.setList(list);
            fav.setItemId(itemId);
            fav.setItemType(itemType);
            favRepo.save(fav);
            return ResponseEntity.ok("Added");
        } catch (Exception e) {
            return ResponseEntity.ok("Already in list");
        }
    }

    @DeleteMapping("/favorites/{itemId}")
    @Transactional
    public ResponseEntity<String> removeFavorite(@RequestHeader("Authorization") String token,
            @PathVariable String itemId,
            @RequestParam(required = false) Long listId) {
        User user = getUserFromToken(token);
        // If listId provided, remove from distinct list. If not, remove from Watchlist
        // (default).
        Long targetListId = listId;
        if (targetListId == null) {
            Optional<UserList> wl = listRepo.findByUserIdAndName(user.getId(), "Watchlist");
            if (wl.isPresent())
                targetListId = wl.get().getId();
            else
                return ResponseEntity.ok("Removed"); // Nothing to remove
        }

        favRepo.deleteByListIdAndItemId(targetListId, itemId);
        return ResponseEntity.ok("Removed");
    }

    @GetMapping("/favorites/check/{itemId}")
    public ResponseEntity<Boolean> checkFavorite(@RequestHeader("Authorization") String token,
            @PathVariable String itemId) {
        // This check is global (is it in ANY list?) OR specific to Watchlist?
        // Let's make it check if it's in the Watchlist for the simple button.
        // Or better: return TRUE if in ANY list.
        try {
            User user = getUserFromToken(token);
            // Since we updated logic to List-based, "existsByUserIdAndItemId" might fetch
            // all.
            // But wait, Repository method logic didn't change for
            // "existsByUserIdAndItemId"?
            // It relies on User field in Favorite. Since we set user field, this still
            // works!
            boolean exists = favRepo.existsByUserIdAndItemId(user.getId(), itemId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}
