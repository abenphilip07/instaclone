package com.example.instaclone.services;

import com.example.instaclone.dtos.CommentDTO;
import com.example.instaclone.entity.Comment;
import com.example.instaclone.entity.Post;
import com.example.instaclone.entity.User;
import com.example.instaclone.exceptions.ResourceNotFoundException;
import com.example.instaclone.repository.CommentRepository;
import com.example.instaclone.repository.PostRepository;
import com.example.instaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // Create a new comment
    public CommentDTO addComment(CommentDTO commentDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal(); // Fetch current user

        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + commentDTO.getPostId()));

        // Create and save a new comment
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setPost(post);
        comment.setUser(currentUser);

        Comment savedComment = commentRepository.save(comment);

        commentDTO.setCommentId(savedComment.getCommentId());
        return commentDTO; // Return the saved comment DTO
    }

    // Update an existing comment
    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Ensure that the current user owns the comment
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("User can only update their own comments.");
        }

        comment.setText(commentDTO.getText());
        commentRepository.save(comment);

        return commentDTO;
    }

    // Delete a comment
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Ensure that the current user owns the comment
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("User can only delete their own comments.");
        }

        commentRepository.delete(comment);
    }

    // Get all comments for a post
    public List<CommentDTO> getCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return commentRepository.findByPostId(postId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Convert Comment entity to CommentDTO
    private CommentDTO toDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .text(comment.getText())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .build();
    }
}
