package com.hackerrank.sample.controller;

import com.hackerrank.sample.audit.AuditService;
import com.hackerrank.sample.dto.ModelOperationRequest;
import com.hackerrank.sample.model.Model;
import com.hackerrank.sample.security.AuthService;
import com.hackerrank.sample.service.ModelService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ModelController {
    
    private static final Logger log = LoggerFactory.getLogger(ModelController.class);
    @Autowired
    private ModelService modelService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "MELI Challenge API - Secure Backend with JWT Authentication";
    }

    /**
     * Crear nuevo modelo
     * Requiere autenticación via JWT token
     * POST /api/v1/models con todos los campos del producto
     */
    @PostMapping("/models")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "modelService", fallbackMethod = "fallbackCreateModel")
    @Retry(name = "modelService")
    public ResponseEntity<?> createModel(
            @RequestBody @Valid ModelOperationRequest request,
            HttpServletRequest httpRequest) {
        
        String username = extractUsername(httpRequest);
        
        try {
            Model model = new Model();
            // Básico
            model.setId(request.getId());
            model.setName(request.getName());
            model.setDescription(request.getDescription());
            model.setCategory(request.getCategory());
            
            // Precios
            model.setPrice(request.getPrice());
            model.setDiscountPercentage(request.getDiscountPercentage());
            model.setInstallmentMonths(request.getInstallmentMonths());
            
            // Características
            model.setColor(request.getColor());
            model.setSize(request.getSize());
            model.setMaterial(request.getMaterial());
            model.setBrand(request.getBrand());
            model.setModel(request.getModel());
            
            // Stock
            model.setStock(request.getStock());
            model.setIsAvailable(request.getIsAvailable());
            
            // Condición
            model.setIsNew(request.getIsNew());
            model.setCondition(request.getCondition());
            
            // Calificación
            model.setRating(request.getRating());
            model.setReviewCount(request.getReviewCount());
            
            // Vendedor
            model.setVendorId(request.getVendorId());
            model.setVendorName(request.getVendorName());
            model.setVendorRating(request.getVendorRating());
            model.setVendorReviewCount(request.getVendorReviewCount());
            
            // Medios de pago
            if(request.getPaymentMethods() != null) {
                model.setPaymentMethods(request.getPaymentMethods());
            }
            
            // Imágenes
            if(request.getImageUrls() != null) {
                model.setImageUrls(request.getImageUrls());
            }
            model.setMainImageUrl(request.getMainImageUrl());
            
            // Información adicional
            model.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
            model.setHasWarranty(request.getHasWarranty());
            model.setWarrantyInfo(request.getWarrantyInfo());
            model.setIsFreeShipping(request.getIsFreeShipping());
            model.setShippingCost(request.getShippingCost());
            model.setEstimatedShippingDays(request.getEstimatedShippingDays());
            model.setSku(request.getSku());
            
            modelService.createModel(model);
            
            auditService.logOperation(
                username, "ModelService", "POST", "/api/v1/models",
                "CREATE_MODEL", "SUCCESS", "201", 
                "Product: " + request.getName() + " | Price: " + request.getPrice()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Modelo creado exitosamente");
            response.put("id", request.getId());
            response.put("name", request.getName());
            response.put("price", request.getPrice());
            response.put("timestamp", LocalDateTime.now());
            
            log.info("[API] Usuario: {} - Creó producto: {} (ID: {}) - Precio: {} - Status: 201", 
                username, request.getName(), request.getId(), request.getPrice());
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (Exception e) {
            auditService.logOperation(
                username, "ModelService", "POST", "/api/v1/models",
                "CREATE_MODEL", "FAILED", "400", e.getMessage()
            );
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            log.error("[API] Usuario: {} - Error creando modelo: {}", username, e.getMessage());
            
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtener todos los modelos
     * Requiere autenticación via JWT token
     * GET /api/v1/models
     */
    @GetMapping("/models")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "modelService", fallbackMethod = "fallbackListModels")
    @Retry(name = "modelService")
    public ResponseEntity<?> getAllModels(HttpServletRequest httpRequest) {
        String username = extractUsername(httpRequest);
        
        try {
            List<Model> models = modelService.getAllModels();
            
            auditService.logOperation(
                username, "ModelService", "GET", "/api/v1/models",
                "LIST_MODELS", "SUCCESS", "200", 
                "Retrieved " + models.size() + " models"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", models);
            response.put("count", models.size());
            response.put("timestamp", LocalDateTime.now());
            
            log.info("[API] Usuario: {} - Listó {} modelos - Status: 200", username, models.size());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            auditService.logOperation(
                username, "ModelService", "GET", "/api/v1/models",
                "LIST_MODELS", "FAILED", "500", e.getMessage()
            );
            
            log.error("[API] Usuario: {} - Error listando modelos: {}", username, e.getMessage());
            return new ResponseEntity<>(
                Map.of("success", false, "message", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Obtener modelo por ID
     * Requiere autenticación via JWT token
     * GET /api/v1/models/details con body {"id": 1}
     */
    @GetMapping("/models/details")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "modelService", fallbackMethod = "fallbackGetModel")
    @Retry(name = "modelService")
    public ResponseEntity<?> getModelById(
            @RequestBody @Valid ModelOperationRequest request,
            HttpServletRequest httpRequest) {
        
        String username = extractUsername(httpRequest);
        
        try {
            Model model = modelService.getModelById(request.getId());
            
            auditService.logOperation(
                username, "ModelService", "POST", "/api/v1/models/details",
                "GET_MODEL", "SUCCESS", "200", "Model ID: " + request.getId()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", model);
            response.put("timestamp", LocalDateTime.now());
            
            log.info("[API] Usuario: {} - Obtuvo modelo ID: {} - Status: 200", username, request.getId());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            auditService.logOperation(
                username, "ModelService", "POST", "/api/v1/models/details",
                "GET_MODEL", "FAILED", "404", "Model ID: " + request.getId()
            );
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            log.error("[API] Usuario: {} - Modelo no encontrado ID: {}", username, request.getId());
            
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Actualizar modelo por ID
     * Requiere autenticación via JWT token
     * PUT /api/v1/models con body con todos los campos a actualizar
     */
    @PutMapping("/models")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "modelService", fallbackMethod = "fallbackUpdateModel")
    @Retry(name = "modelService")
    public ResponseEntity<?> updateModel(
            @RequestBody @Valid ModelOperationRequest request,
            HttpServletRequest httpRequest) {
        
        String username = extractUsername(httpRequest);
        
        try {
            // Obtener modelo existente
            Model model = modelService.getModelById(request.getId());
            
            // Actualizar campos proporcionados
            if(request.getName() != null) model.setName(request.getName());
            if(request.getDescription() != null) model.setDescription(request.getDescription());
            if(request.getCategory() != null) model.setCategory(request.getCategory());
            
            if(request.getPrice() != null) model.setPrice(request.getPrice());
            if(request.getDiscountPercentage() != null) model.setDiscountPercentage(request.getDiscountPercentage());
            if(request.getInstallmentMonths() != null) model.setInstallmentMonths(request.getInstallmentMonths());
            
            if(request.getColor() != null) model.setColor(request.getColor());
            if(request.getSize() != null) model.setSize(request.getSize());
            if(request.getMaterial() != null) model.setMaterial(request.getMaterial());
            if(request.getBrand() != null) model.setBrand(request.getBrand());
            if(request.getModel() != null) model.setModel(request.getModel());
            
            if(request.getStock() != null) model.setStock(request.getStock());
            if(request.getIsAvailable() != null) model.setIsAvailable(request.getIsAvailable());
            
            if(request.getIsNew() != null) model.setIsNew(request.getIsNew());
            if(request.getCondition() != null) model.setCondition(request.getCondition());
            
            if(request.getRating() != null) model.setRating(request.getRating());
            if(request.getReviewCount() != null) model.setReviewCount(request.getReviewCount());
            
            if(request.getVendorId() != null) model.setVendorId(request.getVendorId());
            if(request.getVendorName() != null) model.setVendorName(request.getVendorName());
            if(request.getVendorRating() != null) model.setVendorRating(request.getVendorRating());
            if(request.getVendorReviewCount() != null) model.setVendorReviewCount(request.getVendorReviewCount());
            
            if(request.getPaymentMethods() != null) model.setPaymentMethods(request.getPaymentMethods());
            if(request.getImageUrls() != null) model.setImageUrls(request.getImageUrls());
            if(request.getMainImageUrl() != null) model.setMainImageUrl(request.getMainImageUrl());
            
            if(request.getStatus() != null) model.setStatus(request.getStatus());
            if(request.getHasWarranty() != null) model.setHasWarranty(request.getHasWarranty());
            if(request.getWarrantyInfo() != null) model.setWarrantyInfo(request.getWarrantyInfo());
            if(request.getIsFreeShipping() != null) model.setIsFreeShipping(request.getIsFreeShipping());
            if(request.getShippingCost() != null) model.setShippingCost(request.getShippingCost());
            if(request.getEstimatedShippingDays() != null) model.setEstimatedShippingDays(request.getEstimatedShippingDays());
            if(request.getSku() != null) model.setSku(request.getSku());
            
            modelService.updateModel(model);
            
            auditService.logOperation(
                username, "ModelService", "PUT", "/api/v1/models",
                "UPDATE_MODEL", "SUCCESS", "200", "Model ID: " + request.getId() + " | Name: " + request.getName()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Modelo actualizado exitosamente");
            response.put("id", request.getId());
            response.put("name", request.getName());
            response.put("timestamp", LocalDateTime.now());
            
            log.info("[API] Usuario: {} - Actualizó modelo ID: {} - Nombre: {} - Status: 200", 
                username, request.getId(), request.getName());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            auditService.logOperation(
                username, "ModelService", "PUT", "/api/v1/models",
                "UPDATE_MODEL", "FAILED", "404", e.getMessage()
            );
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            log.error("[API] Usuario: {} - Error actualizando modelo ID: {}", username, request.getId());
            
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Eliminar modelo por ID
     * Requiere autenticación via JWT token
     * DELETE /api/v1/models con body {"id": 1}
     * Retorna 404 si el modelo no existe
     */
    @DeleteMapping("/models")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "modelService", fallbackMethod = "fallbackDeleteModel")
    @Retry(name = "modelService")
    public ResponseEntity<?> deleteModelById(
            @RequestBody @Valid ModelOperationRequest request,
            HttpServletRequest httpRequest) {
        
        String username = extractUsername(httpRequest);
        
        try {
            modelService.deleteModelById(request.getId());
            
            auditService.logOperation(
                username, "ModelService", "DELETE", "/api/v1/models",
                "DELETE_MODEL", "SUCCESS", "200", "Model ID: " + request.getId()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Modelo eliminado exitosamente");
            response.put("id", request.getId());
            response.put("timestamp", LocalDateTime.now());
            
            log.info("[API] Usuario: {} - Eliminó modelo ID: {} - Status: 200", username, request.getId());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            auditService.logOperation(
                username, "ModelService", "DELETE", "/api/v1/models",
                "DELETE_MODEL", "FAILED", "404", e.getMessage()
            );
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            log.error("[API] Usuario: {} - Modelo no encontrado para eliminar ID: {}", username, request.getId());
            
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Limpiar todos los modelos
     * Requiere autenticación via JWT token
     * DELETE /api/v1/models/erase
     */
    @DeleteMapping("/models/erase")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "modelService", fallbackMethod = "fallbackEraseAll")
    @Retry(name = "modelService")
    public ResponseEntity<?> deleteAllModels(HttpServletRequest httpRequest) {
        String username = extractUsername(httpRequest);
        
        try {
            modelService.deleteAllModels();
            
            auditService.logOperation(
                username, "ModelService", "DELETE", "/api/v1/models/erase",
                "ERASE_ALL", "SUCCESS", "200", "All models deleted"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Todos los modelos han sido eliminados");
            response.put("timestamp", LocalDateTime.now());
            
            log.info("[API] Usuario: {} - Eliminó todos los modelos - Status: 200", username);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            auditService.logOperation(
                username, "ModelService", "POST", "/api/v1/models/erase",
                "ERASE_ALL", "FAILED", "500", e.getMessage()
            );
            
            log.error("[API] Usuario: {} - Error eliminando todos los modelos: {}", username, e.getMessage());
            
            return new ResponseEntity<>(
                Map.of("success", false, "message", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // Métodos fallback para Circuit Breaker
    
    public ResponseEntity<?> fallbackCreateModel(ModelOperationRequest request, HttpServletRequest httpRequest, Exception ex) {
        String username = extractUsername(httpRequest);
        log.warn("[API-FALLBACK] Usuario: {} - CircuitBreaker abierto en createModel: {}", username, ex.getMessage());
        return new ResponseEntity<>(
            Map.of("success", false, "message", "Servicio temporalmente no disponible"),
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }
    
    public ResponseEntity<?> fallbackListModels(HttpServletRequest httpRequest, Exception ex) {
        String username = extractUsername(httpRequest);
        log.warn("[API-FALLBACK] Usuario: {} - CircuitBreaker abierto en listModels: {}", username, ex.getMessage());
        return new ResponseEntity<>(
            Map.of("success", false, "message", "Servicio temporalmente no disponible"),
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }
    
    public ResponseEntity<?> fallbackGetModel(ModelOperationRequest request, HttpServletRequest httpRequest, Exception ex) {
        String username = extractUsername(httpRequest);
        log.warn("[API-FALLBACK] Usuario: {} - CircuitBreaker abierto en getModel: {}", username, ex.getMessage());
        return new ResponseEntity<>(
            Map.of("success", false, "message", "Servicio temporalmente no disponible"),
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }
    
    public ResponseEntity<?> fallbackUpdateModel(ModelOperationRequest request, HttpServletRequest httpRequest, Exception ex) {
        String username = extractUsername(httpRequest);
        log.warn("[API-FALLBACK] Usuario: {} - CircuitBreaker abierto en updateModel: {}", username, ex.getMessage());
        return new ResponseEntity<>(
            Map.of("success", false, "message", "Servicio temporalmente no disponible"),
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }
    
    public ResponseEntity<?> fallbackDeleteModel(ModelOperationRequest request, HttpServletRequest httpRequest, Exception ex) {
        String username = extractUsername(httpRequest);
        log.warn("[API-FALLBACK] Usuario: {} - CircuitBreaker abierto en deleteModel: {}", username, ex.getMessage());
        return new ResponseEntity<>(
            Map.of("success", false, "message", "Servicio temporalmente no disponible"),
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }
    
    public ResponseEntity<?> fallbackEraseAll(HttpServletRequest httpRequest, Exception ex) {
        String username = extractUsername(httpRequest);
        log.warn("[API-FALLBACK] Usuario: {} - CircuitBreaker abierto en eraseAll: {}", username, ex.getMessage());
        return new ResponseEntity<>(
            Map.of("success", false, "message", "Servicio temporalmente no disponible"),
            HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    /**
     * Extrae el username del token JWT del header Authorization
     */
    private String extractUsername(HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = authService.getUsernameFromToken(token);
            return username != null ? username : "UNKNOWN";
        }
        return "UNKNOWN";
    }
}
