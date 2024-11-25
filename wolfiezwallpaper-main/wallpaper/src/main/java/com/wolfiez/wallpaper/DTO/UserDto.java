package com.wolfiez.wallpaper.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;
@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private boolean active;
    private MultipartFile profileImage;
    private LocalDateTime createdAt;
    private Set<String> roles;
    private Set<BoardDto> boards;

}
