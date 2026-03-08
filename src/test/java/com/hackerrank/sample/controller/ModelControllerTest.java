package com.hackerrank.sample.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackerrank.sample.Application;
import com.hackerrank.sample.model.Model;
import com.hackerrank.sample.repository.ModelRepository;
import com.hackerrank.sample.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para ModelController.
 *
 * Endpoints cubiertos:
 *   POST   /api/v1/models         - Crear modelo
 *   GET    /api/v1/models         - Listar modelos paginados
 *   GET    /api/v1/models/{id}    - Obtener modelo por ID
 *   PUT    /api/v1/models         - Actualizar modelo (parcial)
 *   DELETE /api/v1/models/{id}    - Eliminar modelo por ID
 *   DELETE /api/v1/models/erase   - Eliminar todos los modelos
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("ModelController - Tests de endpoints CRUD")
class ModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ModelRepository modelRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String authHeader;

    @BeforeEach
    void setUp() {
        // Base de datos limpia antes de cada test
        modelRepository.deleteAll();
        // Token JWT válido para "testuser"
        String token = jwtTokenProvider.generateToken("testuser");
        authHeader = "Bearer " + token;
    }

    // ──────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────

    /** Construye un body JSON válido para POST /api/v1/models (todos los campos @NotNull/@NotBlank del DTO) */
    private Map<String, Object> buildValidCreateRequest(long id) {
        Map<String, Object> body = new HashMap<>();
        // Básico
        body.put("id", id);
        body.put("name", "Laptop Lenovo IdeaPad");
        body.put("description", "Excelente laptop para trabajo y estudio con gran rendimiento");
        body.put("category", "Electrónica");
        // Precios
        body.put("price", new BigDecimal("1500.00"));
        // Características
        body.put("brand", "Lenovo");
        body.put("model", "IdeaPad 3");
        // Stock
        body.put("stock", 10);
        body.put("isAvailable", true);
        // Condición
        body.put("isNew", true);
        body.put("condition", "Nueva");
        // Vendedor
        body.put("vendorId", 1L);
        body.put("vendorName", "Vendedor Oficial");
        // Medios de pago e imágenes
        body.put("paymentMethods", List.of("Tarjeta de Crédito", "Efectivo"));
        body.put("imageUrls", List.of("https://img.example.com/laptop.jpg"));
        body.put("mainImageUrl", "https://img.example.com/laptop.jpg");
        return body;
    }

    /** Inserta directamente un modelo de prueba en la base de datos */
    private void persistModel(long id) {
        Model model = new Model();
        model.setId(id);
        model.setName("Modelo Test " + id);
        model.setDescription("Descripcion del modelo de prueba numero " + id);
        model.setCategory("Categoria Test");
        model.setPrice(new BigDecimal("250.00"));
        model.setBrand("MarcaTest");
        model.setModel("MT-" + id);
        model.setStock(20);
        model.setIsAvailable(true);
        model.setIsNew(true);
        model.setCondition("Nueva");
        model.setVendorId(1L);         // NOT NULL en la entidad
        model.setVendorName("Vendedor Test");
        modelRepository.save(model);
    }

    // ──────────────────────────────────────────────────────────────────
    // POST /api/v1/models
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /models - Body válido retorna 201 con ID del modelo creado")
    void createModel_ValidRequest_Returns201() throws Exception {
        mockMvc.perform(post("/api/v1/models")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidCreateRequest(100L))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.name").value("Laptop Lenovo IdeaPad"));
    }

    @Test
    @DisplayName("POST /models - ID duplicado retorna 400 con success=false")
    void createModel_DuplicateId_Returns400() throws Exception {
        persistModel(200L);

        mockMvc.perform(post("/api/v1/models")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidCreateRequest(200L))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /models - Campos @NotNull/@NotBlank faltantes retorna 400 con errores de validación")
    void createModel_MissingRequiredFields_Returns400WithValidationErrors() throws Exception {
        // Solo envía id y name — faltan description, category, price, brand, model, stock
        Map<String, Object> incompleteBody = Map.of("id", 300L, "name", "Solo nombre");

        mockMvc.perform(post("/api/v1/models")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incompleteBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /models - Sin token JWT retorna 401 Unauthorized")
    void createModel_NoAuth_Returns401() throws Exception {
        mockMvc.perform(post("/api/v1/models")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidCreateRequest(400L))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /models - Token JWT inv\u00e1lido retorna 401 Unauthorized")
    void createModel_InvalidToken_Returns401() throws Exception {
        mockMvc.perform(post("/api/v1/models")
                        .header("Authorization", "Bearer token.invalido.aqui")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidCreateRequest(500L))))
                .andExpect(status().isUnauthorized());
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /api/v1/models
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /models - Lista paginada retorna 200 con metadata de paginación")
    void listModels_Returns200WithPaginationMetadata() throws Exception {
        persistModel(1L);
        persistModel(2L);
        persistModel(3L);

        mockMvc.perform(get("/api/v1/models")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.pagination").exists())
                .andExpect(jsonPath("$.pagination.totalElements").value(3))
                .andExpect(jsonPath("$.pagination.currentPage").value(0));
    }

    @Test
    @DisplayName("GET /models?page=0&size=2 - Paginación custom limita resultados")
    void listModels_WithPageSize2_ReturnsCorrectPage() throws Exception {
        for (long i = 1; i <= 5; i++) persistModel(i);

        mockMvc.perform(get("/api/v1/models")
                        .header("Authorization", authHeader)
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "id")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.pageSize").value(2))
                .andExpect(jsonPath("$.pagination.totalElements").value(5))
                .andExpect(jsonPath("$.pagination.totalPages").value(3))
                .andExpect(jsonPath("$.pagination.hasNext").value(true));
    }

    @Test
    @DisplayName("GET /models - Tabla vacía retorna 200 con lista vacía")
    void listModels_EmptyDatabase_Returns200WithEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/models")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pagination.totalElements").value(0))
                .andExpect(jsonPath("$.data").isArray());
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /api/v1/models/{id}
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /models/{id} - ID existente retorna 200 con datos del modelo")
    void getModelById_ExistingId_Returns200WithModel() throws Exception {
        persistModel(10L);

        mockMvc.perform(get("/api/v1/models/10")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10))
                .andExpect(jsonPath("$.data.name").value("Modelo Test 10"));
    }

    @Test
    @DisplayName("GET /models/{id} - ID inexistente retorna 404 con success=false")
    void getModelById_NonExistingId_Returns404() throws Exception {
        mockMvc.perform(get("/api/v1/models/9999")
                        .header("Authorization", authHeader))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    // ──────────────────────────────────────────────────────────────────
    // PUT /api/v1/models
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /models - Actualización parcial con solo nombre retorna 200")
    void updateModel_PartialUpdateName_Returns200() throws Exception {
        persistModel(20L);

        Map<String, Object> updateBody = Map.of("id", 20L, "name", "Nombre Completamente Nuevo");

        mockMvc.perform(put("/api/v1/models")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").value(20));
    }

    @Test
    @DisplayName("PUT /models - Actualización con múltiples campos retorna 200")
    void updateModel_MultipleFields_Returns200() throws Exception {
        persistModel(21L);

        Map<String, Object> updateBody = new HashMap<>();
        updateBody.put("id", 21L);
        updateBody.put("name", "Nombre Actualizado");
        updateBody.put("price", new BigDecimal("999.99"));
        updateBody.put("stock", 50);
        updateBody.put("category", "Nueva Categoría");

        mockMvc.perform(put("/api/v1/models")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("PUT /models - ID inexistente retorna 404 con success=false")
    void updateModel_NonExistingId_Returns404() throws Exception {
        Map<String, Object> updateBody = Map.of("id", 99999L, "name", "No Existo");

        mockMvc.perform(put("/api/v1/models")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("PUT /models - Body sin campo id retorna 400")
    void updateModel_MissingId_Returns400() throws Exception {
        Map<String, Object> updateBody = Map.of("name", "Sin ID no funciona");

        mockMvc.perform(put("/api/v1/models")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /models - Precio negativo retorna 400 por validación")
    void updateModel_NegativePrice_Returns400() throws Exception {
        persistModel(22L);

        Map<String, Object> updateBody = new HashMap<>();
        updateBody.put("id", 22L);
        updateBody.put("price", new BigDecimal("-50.00"));

        mockMvc.perform(put("/api/v1/models")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isBadRequest());
    }

    // ──────────────────────────────────────────────────────────────────
    // DELETE /api/v1/models/{id}
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /models/{id} - ID existente retorna 200 con confirmación")
    void deleteModelById_ExistingId_Returns200() throws Exception {
        persistModel(30L);

        mockMvc.perform(delete("/api/v1/models/30")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.message").value("Modelo eliminado exitosamente"));
    }

    @Test
    @DisplayName("DELETE /models/{id} - ID inexistente retorna 404 con success=false")
    void deleteModelById_NonExistingId_Returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/models/8888")
                        .header("Authorization", authHeader))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("DELETE /models/{id} - Sin autenticaci\u00f3n retorna 401")
    void deleteModelById_NoAuth_Returns401() throws Exception {
        persistModel(31L);

        mockMvc.perform(delete("/api/v1/models/31"))
                .andExpect(status().isUnauthorized());
    }

    // ──────────────────────────────────────────────────────────────────
    // DELETE /api/v1/models/erase
    // ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /models/erase - Elimina todos los modelos y retorna 200")
    void deleteAllModels_Returns200() throws Exception {
        persistModel(40L);
        persistModel(41L);
        persistModel(42L);

        mockMvc.perform(delete("/api/v1/models/erase")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Todos los modelos han sido eliminados"));
    }

    @Test
    @DisplayName("DELETE /models/erase - Tabla vacía también retorna 200")
    void deleteAllModels_EmptyDatabase_Returns200() throws Exception {
        mockMvc.perform(delete("/api/v1/models/erase")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
