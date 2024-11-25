package com.wolfiez.wallpaper.controller;

import com.wolfiez.wallpaper.DTO.AuthResponse;
import com.wolfiez.wallpaper.DTO.LoginDTO;
import com.wolfiez.wallpaper.DTO.UserRegistrationDTO;
import com.wolfiez.wallpaper.entity.User;
import com.wolfiez.wallpaper.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        User registeredUser = userService.registerUser(registrationDTO);
        return ResponseEntity.ok(new AuthResponse(
                registeredUser.getEmail(),
                registeredUser.getName(),
                true,
                "User registered successfully"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.authenticateUser(loginDTO);
        return ResponseEntity.ok(new AuthResponse(
                user.getEmail(),
                user.getName(),
                true,
                "Login successful"
        ));
    }
}
