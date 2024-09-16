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

    public CommentDTO addComment(Long postId, CommentDTO commentDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Create new Comment and set associations
        Comment comment = new Comment(null, commentDTO.getText(), currentUser, post);

        // Save the new comment to the repository
        Comment savedComment = commentRepository.save(comment);

        // Convert saved comment to DTO
        return toDTO(savedComment);
    }

    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (!comment.getUser().equals(currentUser)) {
            throw new SecurityException("You are not allowed to update this comment.");
        }

        comment.setText(commentDTO.getText());
        return toDTO(commentRepository.save(comment));
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (!comment.getUser().equals(currentUser)) {
            throw new SecurityException("You are not allowed to delete this comment.");
        }

        commentRepository.delete(comment);
    }

    public List<CommentDTO> getCommentsForPost(Long postId) {
        return commentRepository.findByPostId(postId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private CommentDTO toDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .text(comment.getText())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .build();
    }
}
