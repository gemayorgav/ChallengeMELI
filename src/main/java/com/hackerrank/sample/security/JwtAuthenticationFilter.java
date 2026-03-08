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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Filtro que valida JWT tokens en las peticiones
 */
@Component
public class JwtAuthenticationFilter implements Filter {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Rutas públicas que no requieren autenticación
        String requestPath = httpRequest.getRequestURI();
        
        // OpenAPI / Swagger UI
        if (requestPath.startsWith("/swagger-ui") || 
            requestPath.startsWith("/swagger-ui.html") ||
            requestPath.startsWith("/v3/api-docs") ||
            requestPath.startsWith("/swagger-resources") ||
            requestPath.equals("/api/v1/") ||
            requestPath.equals("/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Autenticación (login)
        if (requestPath.startsWith("/auth/") || requestPath.startsWith("/api/v1/auth/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Health checks y métricas públicas
        if (requestPath.startsWith("/api/v1/actuator/health") || 
            requestPath.startsWith("/api/v1/actuator/info")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Obtener token del header Authorization para rutas protegidas
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
        
        // Extraer username del token y crear Authentication
        String username = jwtTokenProvider.getUsernameFromToken(token);
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
    }
}
