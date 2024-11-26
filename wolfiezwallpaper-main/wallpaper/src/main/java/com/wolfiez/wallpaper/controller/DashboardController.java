package com.wolfiez.wallpaper.controller;

import com.wolfiez.wallpaper.DTO.BoardDto;
import com.wolfiez.wallpaper.entity.Board;
import com.wolfiez.wallpaper.security.CustomUserDetails;
import com.wolfiez.wallpaper.service.BoardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


/**
 * Controlador responsable de renderizar el panel de control del usuario.
 *
 * Proporciona un endpoint para mostrar tableros específicos del usuario en el panel de control.
 * Utiliza el mecanismo de autenticación de Spring Security para recuperar detalles del usuario.
 *
 * @author luis
 * @version 1.0
 * @since 25-11-2024
 */
@Controller
public class DashboardController {

    private final BoardService boardService;

    public DashboardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getId();
        List<Board> userBoards = boardService.getUserBoards(userId);

        model.addAttribute("boards", userBoards);
        model.addAttribute("boardDto", new BoardDto());

        return "dashboard";
    }
}