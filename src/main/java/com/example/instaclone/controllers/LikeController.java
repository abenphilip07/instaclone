package com.example.instaclone.controllers;

import com.example.instaclone.dtos.LikeDTO;
import com.example.instaclone.exceptions.ResourceNotFoundException;
import com.example.instaclone.services.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    // Add a like to a post using request body
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<Map<String, String>> addLike(@RequestBody Map<String, Long> requestBody) {
        try {
            Long postId = requestBody.get("postId");
            if (postId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "postId is required"));
            }

            String result = likeService.addLike(postId);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(404).body(Map.of("message", rnfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to add like: " + e.getMessage()));
        }
    }

    // Remove a like from a post using request body
    @DeleteMapping
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Map<String, String>> removeLike(@RequestBody Map<String, Long> requestBody) {
        try {
            Long postId = requestBody.get("postId");
            if (postId == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "postId is required"));
            }

            String result = likeService.removeLike(postId);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(404).body(Map.of("message", rnfe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to remove like: " + e.getMessage()));
        }
    }


    // Get all likes for a post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<LikeDTO>> getLikesForPost(@PathVariable Long postId) {
        try {
            List<LikeDTO> likes = likeService.getLikesForPost(postId);
            return ResponseEntity.ok(likes);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
