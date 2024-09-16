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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeDTO addLike(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Create new Like
        Like like = new Like(null, currentUser, post);
        Like savedLike = likeRepository.save(like);

        return toDTO(savedLike);
    }

    public void removeLike(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Like like = likeRepository.findByPostIdAndUserId(postId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Like not found for postId: " + postId + " and userId: " + currentUser.getId()));
        likeRepository.delete(like);
    }

    public List<LikeDTO> getLikesForPost(Long postId) {
        return likeRepository.findByPostId(postId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private LikeDTO toDTO(Like like) {
        return LikeDTO.builder()
                .likeId(like.getLikeId())
                .postId(like.getPost().getId())
                .userId(like.getUser().getId())
                .build();
    }
}
