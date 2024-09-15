package com.example.instaclone.services;

import com.example.instaclone.dtos.PostDTO;
import com.example.instaclone.entity.Post;
import com.example.instaclone.entity.User;
import com.example.instaclone.exceptions.ResourceNotFoundException;
import com.example.instaclone.repository.PostRepository;
import com.example.instaclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostDTO createPost(PostDTO postDTO) {
        User user = userRepository.findById(postDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + postDTO.getUserId()));

        Post post = new Post(null, postDTO.getUrl(), postDTO.getCaption(), user, null, null);
        return toDTO(postRepository.save(post));
    }

    public PostDTO updatePost(Long postId, PostDTO postDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        post.setUrl(postDTO.getUrl());
        post.setCaption(postDTO.getCaption());
        return toDTO(postRepository.save(post));
    }

    public PostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        return toDTO(post);
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        postRepository.delete(post);
    }

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PostDTO> searchPosts(String query) {
        return postRepository.searchByCaptionOrUsername(query).stream().map(this::toDTO).collect(Collectors.toList());
    }



    private PostDTO toDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .url(post.getUrl())
                .caption(post.getCaption())
                .userId(post.getUser().getId())
                .build();
    }
}
