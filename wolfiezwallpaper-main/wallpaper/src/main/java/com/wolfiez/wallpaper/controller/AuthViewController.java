package com.wolfiez.wallpaper.controller;

import com.wolfiez.wallpaper.DTO.UserRegistrationDTO;
import com.wolfiez.wallpaper.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * Controlador que maneja operaciones de vista relacionadas con la autenticación.
 *
 * Este controlador gestiona la renderización de páginas de registro e inicio de sesión,
 * y procesa el registro de usuarios a través del servicio de usuarios.
 *
 * @author luis
 * @version 1.0
 * @since 25-11-2024
 */
@Controller
public class AuthViewController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // Es importante agregar el objeto DTO al modelo con el nombre correcto
        model.addAttribute("userForm", new UserRegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("userForm") UserRegistrationDTO userForm,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(userForm);
            return "redirect:/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, String error, String logout) {
        if (error != null) {
            model.addAttribute("error", "Invalid credentials");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out");
        }
        return "login";
    }
}