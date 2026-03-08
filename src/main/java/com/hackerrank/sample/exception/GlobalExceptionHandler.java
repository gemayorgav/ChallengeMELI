package com.hackerrank.sample.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Centraliza el manejo de errores y proporciona respuestas consistentes.
 * 
 * Proporciona:
 * - Validación de entrada consistente
 * - Mensajes de error estandarizados
 * - Trazabilidad de errores
 * - Códigos HTTP apropiados
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de validación de DTOs (Bean Validation)
     * 
     * Ejemplo de respuesta:
     * {
     *   "timestamp": "2026-03-08T10:30:00",
     *   "status": 400,
     *   "error": "Validation Error",
     *   "message": "Validation failed for 1 field(s)",
     *   "fieldErrors": {
     *     "price": "must be greater than 0",
     *     "stock": "must not be null"
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Error");
        errorResponse.put("message", String.format("Validation failed for %d field(s)", fieldErrors.size()));
        errorResponse.put("fieldErrors", fieldErrors);

        logger.warn("[VALIDATION_ERROR] Fields: {}", fieldErrors.keySet());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones cuando no se encuentra un recurso (404)
     * 
     * Ejemplo de respuesta:
     * {
     *   "timestamp": "2026-03-08T10:30:00",
     *   "status": 404,
     *   "error": "Not Found",
     *   "message": "No model with given id found."
     * }
     */
    @ExceptionHandler(NoSuchResourceFoundException.class)
    public ResponseEntity<?> handleNoSuchResourceFoundException(
            NoSuchResourceFoundException ex,
            WebRequest request) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", ex.getMessage());

        logger.warn("[NOT_FOUND] {}", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de solicitudes inválidas (400)
     * 
     * Ejemplo de respuesta:
     * {
     *   "timestamp": "2026-03-08T10:30:00",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "message": "Model with given id already exists."
     * }
     */
    @ExceptionHandler(BadResourceRequestException.class)
    public ResponseEntity<?> handleBadResourceRequestException(
            BadResourceRequestException ex,
            WebRequest request) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", ex.getMessage());

        logger.warn("[BAD_REQUEST] {}", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja todas las excepciones no capturadas (500)
     * 
     * Ejemplo de respuesta:
     * {
     *   "timestamp": "2026-03-08T10:30:00",
     *   "status": 500,
     *   "error": "Internal Server Error",
     *   "message": "An unexpected error occurred. Please contact support."
     * }
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(
            Exception ex,
            WebRequest request) {

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "An unexpected error occurred. Please contact support.");

        logger.error("[INTERNAL_ERROR] Unexpected exception: ", ex);

        // En desarrollo, incluir detalles del error
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");
        if ("dev".equals(profile) || profile == null) {
            errorResponse.put("exceptionType", ex.getClass().getSimpleName());
            errorResponse.put("exceptionMessage", ex.getMessage());
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
