package com.wolfiez.wallpaper.controller;


import com.wolfiez.wallpaper.DTO.BoardDto;
import com.wolfiez.wallpaper.DTO.BoardResponseDto;
import com.wolfiez.wallpaper.entity.Board;
import com.wolfiez.wallpaper.exception.BoardNotFoundException;
import com.wolfiez.wallpaper.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }
    @PostMapping(value = "/{boardId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Board> uploadBoardImage(
            @PathVariable Long boardId,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            Board updatedBoard = boardService.updateBoardImage(boardId, imageFile);
            return ResponseEntity.ok(updatedBoard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{boardId}/image")
    public ResponseEntity<byte[]> getBoardImage(@PathVariable Long boardId) {
        try {
            byte[] imageBytes = boardService.getBoardImage(boardId);
            if (imageBytes == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{boardId}/image")
    public ResponseEntity<Void> deleteBoardImage(@PathVariable Long boardId) {
        try {
            boardService.removeBoardImage(boardId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardDto boardDto) {
        try {
            if (boardDto.getUserId() == null) {
                return ResponseEntity.badRequest().build();
            }

            Board createdBoard = boardService.createBoard(boardDto, boardDto.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BoardResponseDto.fromBoard(createdBoard));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoard(@PathVariable Long id) {
        Board board = boardService.getBoard(id);
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @PathVariable Long id,
            @RequestBody BoardDto boardDto) {
        try {
            Board updatedBoard = boardService.updateBoard(id, boardDto);
            return ResponseEntity.ok(BoardResponseDto.fromBoard(updatedBoard));
        } catch (BoardNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        try {
            boardService.deleteBoard(id);
            return ResponseEntity.ok().build();
        } catch (BoardNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<BoardResponseDto> getBoardDetails(@PathVariable Long id) {
        try {
            Board board = boardService.getBoard(id);
            BoardResponseDto dto = BoardResponseDto.fromBoard(board);
            return ResponseEntity.ok(dto);
        } catch (BoardNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Board>> getUserBoards(@PathVariable Long userId) {
        List<Board> userBoards = boardService.getUserBoards(userId);
        return new ResponseEntity<>(userBoards, HttpStatus.OK);
    }

    @GetMapping("/public-boards")
    public String showPublicBoards(Model model) {
        List<Board> publicBoards = boardService.getPublicBoards();
        model.addAttribute("boards", publicBoards);
        return "public-boards";
    }

    @GetMapping("/search")
    public ResponseEntity<List<Board>> searchUserBoards(@RequestHeader("userId") Long userId, @RequestParam("keyword") String keyword) {
        List<Board> searchResults = boardService.searchUserBoards(userId, keyword);
        return new ResponseEntity<>(searchResults, HttpStatus.OK);
    }

}
