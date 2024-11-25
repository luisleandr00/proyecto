package com.wolfiez.wallpaper.controller;

import com.wolfiez.wallpaper.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicBoardsController {

    private final BoardService boardService;

    public PublicBoardsController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/boards/public")
    public String showPublicBoards(Model model) {
        model.addAttribute("boards", boardService.getPublicBoards());
        return "public-boards";
    }
}

