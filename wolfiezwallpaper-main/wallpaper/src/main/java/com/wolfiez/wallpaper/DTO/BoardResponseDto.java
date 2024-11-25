package com.wolfiez.wallpaper.DTO;

import com.wolfiez.wallpaper.entity.Board;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BoardResponseDto {
    private Long id;
    private String name;
    private String description;
    private boolean isPrivate;
    private LocalDateTime createdAt;
    private Long userId;

    public static BoardResponseDto fromBoard(Board board) {
        BoardResponseDto dto = new BoardResponseDto();
        dto.setId(board.getId());
        dto.setName(board.getName());
        dto.setDescription(board.getDescription());
        dto.setPrivate(board.isPrivate());
        dto.setCreatedAt(board.getCreatedAt());
        dto.setUserId(board.getUserId());
        return dto;
    }
}