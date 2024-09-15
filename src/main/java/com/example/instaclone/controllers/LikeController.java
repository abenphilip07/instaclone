package com.example.instaclone.controllers;

import com.example.instaclone.dtos.LikeDTO;
import com.example.instaclone.services.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeDTO> addLike(@Validated @RequestBody LikeDTO likeDTO) {
        return ResponseEntity.ok(likeService.addLike(likeDTO.getPostId(), likeDTO.getUserId()));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeLike(@Validated @RequestBody LikeDTO likeDTO) {
        likeService.removeLike(likeDTO.getPostId(), likeDTO.getUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<LikeDTO>> getLikesForPost(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getLikesForPost(postId));
    }
}
