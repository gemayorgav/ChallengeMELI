package com.hackerrank.sample.security;

import com.hackerrank.sample.model.User;
import com.hackerrank.sample.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio de autenticación
 */
@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    /**
     * Autentica usuario y devuelve token
     */
    public AuthResponse authenticate(AuthRequest request) {
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        
        if (user.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        User foundUser = user.get();
        
        // Validación simple de contraseña (en producción usar bcrypt)
        if (!foundUser.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        if (!foundUser.isEnabled()) {
            throw new RuntimeException("Usuario deshabilitado");
        }
        
        String token = jwtTokenProvider.generateToken(foundUser.getUsername());
        
        return new AuthResponse(token, foundUser.getUsername(), foundUser.getRole());
    }
    
    /**
     * Valida si un token es válido
     */
    public boolean isTokenValid(String token) {
        return jwtTokenProvider.validateToken(token);
    }
    
    /**
     * Obtiene el username del token
     */
    public String getUsernameFromToken(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }
}
