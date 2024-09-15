package com.example.instaclone.repository;

import com.example.instaclone.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    List<Comment> findByPostId(Long postId);


    Optional<Comment> findByPostIdAndUserId(Long postId, Long userId);
}
