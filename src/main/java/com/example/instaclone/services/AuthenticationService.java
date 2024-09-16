package com.example.instaclone.services;


import com.example.instaclone.dtos.LoginUserDto;
import com.example.instaclone.dtos.RegisterUserDto;
import com.example.instaclone.entity.User;
import com.example.instaclone.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto userAddRequestDTO) {
        User user = new User();
        user.setName(userAddRequestDTO.getFullName());
        user.setEmail(userAddRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userAddRequestDTO.getPassword()));
        user.setRole(userAddRequestDTO.getRole());

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
