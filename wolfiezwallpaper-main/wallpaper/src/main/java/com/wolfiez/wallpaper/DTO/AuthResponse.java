package com.wolfiez.wallpaper.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String email;
    private String name;
    private boolean success;
    private String message;
}
