package com.example.instaclone.controllers;

import com.example.instaclone.dtos.LoginUserDto;
import com.example.instaclone.dtos.RegisterUserDto;
import com.example.instaclone.entity.User;
import com.example.instaclone.services.AuthenticationService;
import com.example.instaclone.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationService authenticationService, JwtService jwtService, UserDetailsService userDetailsService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Sign-Up Endpoint
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDto registerUserDto) {
        try {
            User user = authenticationService.signup(registerUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully with ID: " + user.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error during registration: " + e.getMessage());
        }
    }

    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginUserDto loginUserDto) {
        try {
            // Authenticate the user
            User user = authenticationService.authenticate(loginUserDto);

            // Load the user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

            // Generate JWT token
            String jwtToken = jwtService.generateToken(userDetails);

            return ResponseEntity.ok("Login successful! JWT Token: " + jwtToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }
    }
}
