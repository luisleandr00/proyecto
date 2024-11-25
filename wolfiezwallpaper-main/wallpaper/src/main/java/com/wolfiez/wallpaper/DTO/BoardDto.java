package com.wolfiez.wallpaper.DTO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BoardDto {
    private Long id;
    private String name;
    private String description;
    private String image;
    private boolean isPrivate;
    private LocalDateTime createdAt;
    private Long userId;
}