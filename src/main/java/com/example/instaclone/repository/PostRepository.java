package com.example.instaclone.repository;

import com.example.instaclone.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Search by caption or username
    @Query("SELECT p FROM Post p WHERE p.caption LIKE %:query% OR p.user.name LIKE %:query%")
    List<Post> searchByCaptionOrUsername(@Param("query") String query);
}
