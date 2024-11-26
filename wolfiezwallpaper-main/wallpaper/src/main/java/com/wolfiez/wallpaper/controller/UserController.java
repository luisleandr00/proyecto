package com.wolfiez.wallpaper.controller;

import com.wolfiez.wallpaper.DTO.UserDto;
import com.wolfiez.wallpaper.entity.User;
import com.wolfiez.wallpaper.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


/**
 * Controlador REST para gestionar operaciones relacionadas con usuarios.
 *
 * Proporciona endpoints para gestionar usuarios, incluyendo:
 * - Carga y gestión de imágenes de perfil
 * - Recuperación de información de usuario
 * - Actualización y eliminación de usuarios
 * - Búsqueda de usuarios
 *
 * @author luis
 * @version 1.0
 * @since 25-11-2024
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    /**
     * Carga una imagen de perfil para un usuario específico.
     *
     * @param userId El identificador único del usuario
     * @param imageFile El archivo de imagen para el perfil de usuario
     * @return ResponseEntity con el Usuario actualizado o un estado de error
     */
    @PostMapping("/{userId}/profile-image")
    public ResponseEntity<User> uploadProfileImage(
            @PathVariable Long userId,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            User updatedUser = userService.updateProfileImage(userId, imageFile);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Recupera la imagen de perfil de un usuario específico.
     *
     * @param userId El identificador único del usuario
     * @return ResponseEntity que contiene los bytes de la imagen de perfil
     */
    @GetMapping("/{userId}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long userId) {
        try {
            byte[] imageBytes = userService.getProfileImage(userId);
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

    /**
     * Elimina la imagen de perfil de un usuario.
     *
     * @param userId El identificador único del usuario
     * @return ResponseEntity indicando el resultado de la operación
     */
    @DeleteMapping("/{userId}/profile-image")
    public ResponseEntity<Void> deleteProfileImage(@PathVariable Long userId) {
        try {
            userService.removeProfileImage(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Actualiza la información de un usuario.
     *
     * Permite actualizar detalles del usuario, imagen de perfil y contraseña.
     *
     * @param id El identificador único del usuario
     * @param userDto Objeto de transferencia de datos con la información de usuario
     * @param profileImage Archivo de imagen de perfil opcional
     * @param newPassword Nueva contraseña opcional
     * @return ResponseEntity con el Usuario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @ModelAttribute UserDto userDto,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "newPassword", required = false) String newPassword
    ) {
        User updatedUser = userService.updateUser(id, userDto, profileImage, newPassword);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }



    /**
     * Elimina un usuario del sistema.
     *
     * @param id El identificador único del usuario a eliminar
     * @return ResponseEntity indicando el resultado de la operación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    /**
     * Busca usuarios basándose en una palabra clave.
     *
     * @param keyword Término de búsqueda para encontrar usuarios
     * @return ResponseEntity con la lista de Usuarios que coinciden
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("keyword") String keyword) {
        List<User> searchResults = userService.searchUsers(keyword);
        return new ResponseEntity<>(searchResults, HttpStatus.OK);
    }

    /**
     * Recupera usuarios por su rol específico.
     *
     * @param roleName Nombre del rol para filtrar usuarios
     * @return ResponseEntity con la lista de Usuarios que tienen el rol especificado
     */
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String roleName) {
        List<User> usersByRole = userService.getUsersByRole(roleName);
        return new ResponseEntity<>(usersByRole, HttpStatus.OK);
    }
}