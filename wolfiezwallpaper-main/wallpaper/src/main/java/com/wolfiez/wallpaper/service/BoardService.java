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

/**
 * Service class for managing board-related operations.
 * Handles creation, retrieval, update, and deletion of boards.
 *
 * @author luis
 * @version 1.0
 * @since 25-11-2024
 */
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

    /**
     * Updates the image for a specific board.
     *
     * @param boardId Unique identifier of the board
     * @param imageFile Multipart image file to be stored
     * @return Updated Board entity
     * @throws IOException If there's an error processing the image
     */
    public Board updateBoardImage(Long boardId, MultipartFile imageFile) throws IOException {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        String encodedImage = imageService.processImageForStorage(imageFile);
        board.setImage(encodedImage);

        return boardRepository.save(board);
    }

    /**
     * Retrieves the image for a specific board.
     *
     * @param boardId Unique identifier of the board
     * @return Byte array representing the board's image, or null if no image exists
     */
    public byte[] getBoardImage(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        if (board.getImage() == null) {
            return null;
        }

        return imageService.decodeImage(board.getImage());
    }

    /**
     * Removes the image from a specific board.
     *
     * @param boardId Unique identifier of the board
     */
    public void removeBoardImage(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        board.setImage(null);
        boardRepository.save(board);
    }

    /**
     * Creates a new board for a specific user.
     *
     * @param boardDto Data transfer object containing board details
     * @param userId Unique identifier of the user creating the board
     * @return Newly created Board entity
     * @throws IllegalArgumentException If user is not found
     */
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

    /**
     * Retrieves a board by its unique identifier.
     *
     * @param id Unique board identifier
     * @return Board entity
     * @throws BoardNotFoundException If board is not found
     */
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