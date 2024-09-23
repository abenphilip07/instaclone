package com.example.instaclone.services;

import com.example.instaclone.dtos.LikeDTO;
import com.example.instaclone.entity.Like;
import com.example.instaclone.entity.Post;
import com.example.instaclone.entity.User;
import com.example.instaclone.exceptions.ResourceNotFoundException;
import com.example.instaclone.repository.LikeRepository;
import com.example.instaclone.repository.PostRepository;
import com.example.instaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // Add a like to a post
    public String addLike(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal(); // Fetch current user

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Check if the user already liked the post
        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, currentUser.getId());
        if (existingLike.isPresent()) {
            return "You have already liked this post.";
        }

        // Create a new like
        Like like = new Like();
        like.setUser(currentUser);
        like.setPost(post);

        likeRepository.save(like);

        return "Like added successfully.";
    }

    // Remove a like from a post
    public String removeLike(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Like like = likeRepository.findByPostIdAndUserId(postId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Like not found for postId: " + postId + " and userId: " + currentUser.getId()));

        likeRepository.delete(like);
        return "Like removed successfully.";
    }

    // Get all likes for a post
    public List<LikeDTO> getLikesForPost(Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return likeRepository.findByPostId(postId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Convert Like entity to LikeDTO
    private LikeDTO toDTO(Like like) {
        return LikeDTO.builder()
                .likeId(like.getLikeId())
                .postId(like.getPost().getId())
                .userId(like.getUser().getId())
                .build();
    }
}
