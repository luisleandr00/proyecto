package com.wolfiez.wallpaper.service;

import com.wolfiez.wallpaper.DTO.BoardDto;
import com.wolfiez.wallpaper.entity.Board;
import com.wolfiez.wallpaper.entity.User;
import com.wolfiez.wallpaper.exception.BoardNotFoundException;
import com.wolfiez.wallpaper.exception.UserNotFoundException;
import com.wolfiez.wallpaper.repository.BoardRepository;
import com.wolfiez.wallpaper.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class BoardService {
    @Autowired
    private ImageService imageService;

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;


    public BoardService(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }
    public Board updateBoardImage(Long boardId, MultipartFile imageFile) throws IOException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        String encodedImage = imageService.processImageForStorage(imageFile);
        board.setImage(encodedImage);

        return boardRepository.save(board);
    }

    public byte[] getBoardImage(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        if (board.getImage() == null) {
            return null;
        }

        return imageService.decodeImage(board.getImage());
    }
    public void removeBoardImage(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        board.setImage(null);
        boardRepository.save(board);
    }
    public Board createBoard(BoardDto boardDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Board board = new Board();
        board.setName(boardDto.getName());
        board.setDescription(boardDto.getDescription());
        board.setPrivate(boardDto.isPrivate());
        board.setUser(user);

        return boardRepository.save(board);
    }

    public Board getBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("Board not found with id: " + id));
    }

    public List<Board> getUserBoards(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return boardRepository.findByUser(user);
    }

    public List<Board> getPublicBoards() {
        return boardRepository.findPublicBoards();
    }

    public List<Board> searchUserBoards(Long userId, String keyword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return boardRepository.searchUserBoards(user, keyword);
    }

    public Board updateBoard(Long id, BoardDto boardDto) {
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("Board not found with id: " + id));

        if (!existingBoard.getUser().getId().equals(boardDto.getUserId())) {
            throw new IllegalArgumentException("User does not have permission to edit this board");
        }

        existingBoard.setName(boardDto.getName());
        existingBoard.setDescription(boardDto.getDescription());
        existingBoard.setPrivate(boardDto.isPrivate());

        return boardRepository.save(existingBoard);
    }

    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("Board not found with id: " + id));
        boardRepository.delete(board);
    }
}