package com.example.instaclone.controllers;

import com.example.instaclone.dtos.PostDTO;
import com.example.instaclone.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final String uploadDirectory = "src/main/java/com/example/instaclone/postsImgs/"; // Include path separator at the end

    @PostMapping("/upload")
    public ResponseEntity<PostDTO> uploadPostImage(@RequestParam("file") MultipartFile file,
                                                   @RequestParam("caption") String caption,
                                                   @RequestParam("userId") Long userId) {
        try {
            // Ensure the directory exists
            Path uploadPath = Paths.get(uploadDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the image to the server
            String filename = file.getOriginalFilename();
            if (filename == null || filename.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            Path path = uploadPath.resolve(filename); // Safely build the full file path
            Files.write(path, file.getBytes());

            // Generate the image URL
            String imageUrl = "http://localhost:8080/posts/images/" + filename;

            // Create PostDTO with the image URL
            PostDTO postDTO = new PostDTO();
            postDTO.setUrl(imageUrl);
            postDTO.setCaption(caption);
            postDTO.setUserId(userId);

            // Save post using PostService
            return ResponseEntity.ok(postService.createPost(postDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path path = Paths.get(uploadDirectory).resolve(filename);
            Resource resource = new UrlResource(path.toUri());

            // Determine the file's content type
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @Validated @RequestBody PostDTO postDTO) {
        return ResponseEntity.ok(postService.updatePost(id, postDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestParam String query) {
        return ResponseEntity.ok(postService.searchPosts(query));
    }
}
