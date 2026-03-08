package com.hackerrank.sample.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackerrank.sample.Application;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para AuthController.
 * Valida el endpoint de autenticación POST /api/v1/auth/login.
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("AuthController - Tests de autenticación")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========== POST /api/v1/auth/login ==========

    @Test
    @DisplayName("login - Credenciales válidas retorna 200 con token JWT")
    void login_ValidCredentials_Returns200WithToken() throws Exception {
        Map<String, String> loginBody = Map.of("username", "testuser", "password", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value("1 hora"));
    }

    @Test
    @DisplayName("login - Usuario admin con credenciales válidas retorna 200")
    void login_AdminCredentials_Returns200WithToken() throws Exception {
        Map<String, String> loginBody = Map.of("username", "admin", "password", "admin123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @DisplayName("login - Contraseña incorrecta retorna 401")
    void login_InvalidPassword_Returns401() throws Exception {
        Map<String, String> loginBody = Map.of("username", "testuser", "password", "contraseniaErronea");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("login - Usuario inexistente retorna 401")
    void login_UnknownUser_Returns401() throws Exception {
        Map<String, String> loginBody = Map.of("username", "usuario_que_no_existe", "password", "cualquierClave");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
