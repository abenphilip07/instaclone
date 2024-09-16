package com.example.instaclone.dtos;


import com.example.instaclone.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {
    private String fullName;
    private String email;
    private String password;
    private Role role;


}
