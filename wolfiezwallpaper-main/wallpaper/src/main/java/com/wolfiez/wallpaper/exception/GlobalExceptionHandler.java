package com.wolfiez.wallpaper.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la aplicación de Wallpaper.
 *
 * Esta clase utiliza la anotación {@code @ControllerAdvice} para manejar
 * excepciones en toda la aplicación de manera centralizada.
 *
 * Proporciona métodos de manejo de excepciones personalizadas y
 * sobrescribe algunos métodos de manejo de excepciones estándar de Spring.
 *
 * @author luis
 * @version 1.0
 * @since 25-11-2024
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     * Maneja las excepciones de tipo UserNotFoundException.
     *
     * Cuando un usuario no es encontrado, este
     * metodo genera una respuesta
     * HTTP con estado 404 (NOT FOUND) y detalles del error.
     *
     * @param ex La excepción de usuario no encontrado
     * @param request La solicitud web que originó la excepción
     * @return ResponseEntity con detalles del error y código de estado 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


    /**
     * Maneja las excepciones de tipo BoardNotFoundException.
     *
     * Cuando un board no es encontrado, este método genera una respuesta
     * HTTP con estado 404 (NOT FOUND) y detalles del error.
     *
     * @param ex La excepción de board no encontrado
     * @param request La solicitud web que originó la excepción
     * @return ResponseEntity con detalles del error y código de estado 404
     */
    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<Object> handleBoardNotFoundException(
            BoardNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja las excepciones de tipo DuplicateResourceException.
     *
     * Cuando se intenta crear un recurso duplicado, este método genera
     * una respuesta HTTP con estado 400 (BAD REQUEST) y detalles del error.
     *
     * @param ex La excepción de recurso duplicado
     * @param request La solicitud web que originó la excepción
     * @return ResponseEntity con detalles del error y código de estado 400
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Manejador de último recurso para excepciones no capturadas.
     *
     * Captura cualquier excepción no manejada específicamente y
     * genera una respuesta HTTP con estado 500 (INTERNAL SERVER ERROR).
     *
     * @param ex La excepción no capturada
     * @param request La solicitud web que originó la excepción
     * @return ResponseEntity con detalles del error genérico y código de estado 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "An unexpected error occurred");
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Maneja las excepciones de procesamiento de imagen.
     *
     * Cuando ocurre un error al procesar una imagen, este método genera
     * una respuesta HTTP con estado 500 (INTERNAL SERVER ERROR) y detalles del error.
     *
     * @param ex La excepción de procesamiento de imagen
     * @param request La solicitud web que originó la excepción
     * @return ResponseEntity con detalles del error y código de estado 500
     */

    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<Object> handleImageProcessingException(
            ImageProcessingException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /**
     * Maneja las excepciones de imagen inválida.
     *
     * Cuando se intenta procesar una imagen que no cumple con los
     * requisitos, este método genera una respuesta HTTP con estado
     * 400 (BAD REQUEST) y detalles del error.
     *
     * @param ex La excepción de imagen inválida
     * @param request La solicitud web que originó la excepción
     * @return ResponseEntity con detalles del error y código de estado 400
     */
    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<Object> handleInvalidImageException(
            InvalidImageException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    /**
     * Maneja las excepciones de autenticación.
     *
     * Cuando ocurre un error durante el proceso de autenticación,
     * este método genera una respuesta HTTP con estado 401 (UNAUTHORIZED)
     * y detalles del error.
     *
     * @param ex La excepción de autenticación
     * @param request La solicitud web que originó la excepción
     * @return ResponseEntity con detalles del error y código de estado 401
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", HttpStatus.UNAUTHORIZED.value());

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Sobrescribe el método de manejo de errores de validación de argumentos.
     *
     * Captura y procesa errores de validación de métodos, generando
     * una respuesta detallada con todos los errores de validación.
     *
     * @param ex Excepción de argumento no válido
     * @param headers Encabezados HTTP
     * @param status Código de estado HTTP
     * @param request Solicitud web
     * @return ResponseEntity con lista de errores de validación y código de estado 400
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getField() + ": " + x.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
