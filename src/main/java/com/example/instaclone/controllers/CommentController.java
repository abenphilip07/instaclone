package com.example.instaclone.controllers;

import com.example.instaclone.dtos.CommentDTO;
import com.example.instaclone.exceptions.ResourceNotFoundException;
import com.example.instaclone.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // Add a new comment to a post
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<?> addComment(@Validated @RequestBody CommentDTO commentDTO) {
        try {
            CommentDTO savedComment = commentService.addComment(commentDTO);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add comment: " + e.getMessage());
        }
    }

    // Update an existing comment
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:edit')")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @Validated @RequestBody CommentDTO commentDTO) {
        try {
            CommentDTO updatedComment = commentService.updateComment(id, commentDTO);
            return ResponseEntity.ok(updatedComment);
        } catch (SecurityException se) {
            return ResponseEntity.status(403).body(se.getMessage());
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(404).body(rnfe.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update comment: " + e.getMessage());
        }
    }

    // Delete a comment
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (SecurityException se) {
            return ResponseEntity.status(403).body(se.getMessage());
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(404).body(rnfe.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete comment: " + e.getMessage());
        }
    }

    // Get all comments for a post
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsForPost(@PathVariable Long postId) {
        try {
            List<CommentDTO> comments = commentService.getCommentsForPost(postId);
            return ResponseEntity.ok(comments);
        } catch (ResourceNotFoundException rnfe) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
