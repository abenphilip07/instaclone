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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeDTO addLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Create new Like
        Like like = new Like(null, user, post);
        Like savedLike = likeRepository.save(like);

        return toDTO(savedLike);
    }

    public void removeLike(Long postId, Long userId) {
        Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found for postId: " + postId + " and userId: " + userId));
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
