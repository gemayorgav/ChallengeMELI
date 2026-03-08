package com.hackerrank.sample.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO para solicitudes de autenticación.
 * 
 * Usuarios de prueba disponibles:
 * - username: testuser / password: password123
 * - username: admin / password: admin123
 */
@Data
@Schema(
    description = "Solicitud de autenticación con credenciales",
    example = "{\n  \"username\": \"testuser\",\n  \"password\": \"password123\"\n}"
)
public class AuthRequest {
    
    @Schema(
        description = "Nombre de usuario",
        example = "testuser",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;
    
    @Schema(
        description = "Contraseña",
        example = "password123",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
    
    public AuthRequest() {
    }
    
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
