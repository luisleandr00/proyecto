package com.wolfiez.wallpaper.controller;

import com.wolfiez.wallpaper.DTO.UserDto;
import com.wolfiez.wallpaper.entity.User;
import com.wolfiez.wallpaper.exception.UserNotFoundException;
import com.wolfiez.wallpaper.repository.UserRepository;
import com.wolfiez.wallpaper.service.UserService;
import com.wolfiez.wallpaper.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


/**
 * Controlador que gestiona operaciones relacionadas con el perfil de usuario.
 *
 * Maneja la visualización de perfiles, actualización y funcionalidad de carga de imágenes.
 * Utiliza Spring Security para la autenticación y recuperación del contexto de usuario.
 *
 * @author luis
 * @version 1.0
 * @since 25-11-2024
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    UserRepository userRepository;

    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public ProfileController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }


    /**
     * Recupera y muestra el perfil del usuario.
     *
     * @param userDetails Detalles de usuario de Spring Security del usuario autenticado
     * @param model Modelo de Spring MVC para agregar atributos a la vista
     * @return Nombre de la vista para renderizar la página de perfil
     */
    @GetMapping
    public String getProfile(@AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            model.addAttribute("user", user);
            return "profile";
        } catch (UserNotFoundException e) {
            return "redirect:/login";
        }
    }


    /**
     * Actualiza la información del perfil de usuario.
     *
     * Admite la actualización de detalles de usuario, imagen de perfil y contraseña.
     * Incluye validación para confirmación de contraseña.
     *
     * @param userDto Objeto de transferencia de datos que contiene información de actualización de usuario
     * @param profileImage Archivo multipart opcional para la imagen de perfil
     * @param newPassword Contraseña nueva opcional para el usuario
     * @param confirmPassword Confirmación de contraseña para prevenir errores
     * @param authentication Objeto de Autenticación de Spring Security
     * @param redirectAttributes Atributos de redirección de Spring MVC para mensajes flash
     * @return Ruta de redirección basada en el éxito o fallo de la actualización
     */
    @PostMapping("/update")
    public String updateProfile(
            @ModelAttribute UserDto userDto,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        // Validate password match if new password is provided
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/profile/edit";
            }
        }

        try {
            // Get current user details from authentication
            String email = authentication.getName();
            User currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            // Update user profile
            userService.updateUser(currentUser.getId(), userDto, profileImage, newPassword);

            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
            return "redirect:/profile/edit";
        }
    }

    // Helper method to get current user ID
    private Long getCurrentUserId(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getId();
    }
}