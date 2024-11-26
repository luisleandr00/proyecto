package com.wolfiez.wallpaper.controller;

import com.wolfiez.wallpaper.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador responsable de mostrar los tableros públicos.
 *
 * Proporciona un endpoint para recuperar y mostrar todos los tableros
 * que están marcados como públicos en la aplicación.
 *
 * @author luis
 * @version 1.0
 * @since 25-11-2024
 */
@Controller
public class PublicBoardsController {

    private final BoardService boardService;

    public PublicBoardsController(BoardService boardService) {
        this.boardService = boardService;
    }

    /**
     * Muestra los tableros públicos en la vista.
     *
     * Recupera todos los tableros públicos utilizando el servicio de tableros
     * y los agrega al modelo para ser renderizados en la vista.
     *
     * @param model Modelo de Spring MVC para agregar atributos a la vista
     * @return Nombre de la vista de tableros públicos
     */
    @GetMapping("/boards/public")
    public String showPublicBoards(Model model) {
        model.addAttribute("boards", boardService.getPublicBoards());
        return "public-boards";
    }
}

