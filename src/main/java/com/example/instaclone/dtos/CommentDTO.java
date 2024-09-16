package com.example.instaclone.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long commentId;

    @NotBlank(message = "Text cannot be blank")
    private String text;

    private Long userId;

    @NotNull(message = "Post ID cannot be null")
    private Long postId;
}
