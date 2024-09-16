package com.example.instaclone.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;

    @NotBlank(message = "URL cannot be blank")
    private String url;

    @Size(max = 500, message = "Caption cannot exceed 500 characters")
    private String caption;

    private Long userId;

    private List<Long> commentIds;
    private List<Long> likeIds;
}
