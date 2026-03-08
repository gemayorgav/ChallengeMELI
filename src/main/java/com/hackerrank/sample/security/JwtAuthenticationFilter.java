package com.hackerrank.sample.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro que valida JWT tokens en las peticiones
 */
@Component
public class JwtAuthenticationFilter implements Filter {
    
    @Autowired
    private AuthService authService;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Permitir endpoints públicos sin autenticación
        String requestPath = httpRequest.getRequestURI();
        if (requestPath.startsWith("/auth/") || requestPath.equals("/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Obtener token del header Authorization
        String authHeader = httpRequest.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no proporcionado");
            return;
        }
        
        String token = authHeader.substring(7); // Remover "Bearer "
        
        if (!authService.isTokenValid(token)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
            return;
        }
        
        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    }
}
