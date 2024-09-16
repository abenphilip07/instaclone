package com.example.instaclone.controllers;

import com.example.instaclone.dtos.LikeDTO;
import com.example.instaclone.services.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LikeDTO> addLike(@Validated @RequestBody LikeDTO likeDTO) {
        return ResponseEntity.ok(likeService.addLike(likeDTO.getPostId()));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeLike(@Validated @RequestBody LikeDTO likeDTO) {
        likeService.removeLike(likeDTO.getPostId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<LikeDTO>> getLikesForPost(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getLikesForPost(postId));
    }
}
