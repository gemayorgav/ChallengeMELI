package com.hackerrank.sample.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para crear/actualizar modelos con validación completa.
 * 
 * Validaciones implementadas:
 * - Campos requeridos para creación
 * - Rangos de valores permitidos
 * - Formatos de datos validados
 * - Longitud de strings controlada
 * - Valores monetarios validados
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelOperationRequest {
    
    // ===== BÁSICO =====
    @NotNull(message = "El ID del modelo no puede ser nulo")
    @Positive(message = "El ID debe ser un número positivo")
    private Long id;
    
    @NotBlank(message = "El nombre del producto no puede estar vacio")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    private String name;
    
    @NotBlank(message = "La descripción no puede estar vacia")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String description;
    
    @NotBlank(message = "La categoría no puede estar vacia")
    @Size(min = 2, max = 100, message = "La categoría debe tener entre 2 y 100 caracteres")
    private String category;
    
    // ===== PRECIOS =====
    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999999.99", message = "El precio no puede exceder 999999999.99")
    private BigDecimal price;
    
    @DecimalMin(value = "0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100", message = "El descuento no puede ser mayor a 100%")
    private BigDecimal discountPercentage;
    
    @Min(value = 1, message = "Las cuotas deben ser al menos 1")
    @Max(value = 60, message = "Las cuotas no pueden ser más de 60")
    private Integer installmentMonths;
    
    // ===== CARACTERÍSTICAS =====
    @Size(max = 50, message = "El color debe tener máximo 50 caracteres")
    private String color;
    
    @Size(max = 50, message = "El tamaño debe tener máximo 50 caracteres")
    private String size;
    
    @Size(max = 50, message = "El material debe tener máximo 50 caracteres")
    private String material;
    
    @NotBlank(message = "La marca no puede estar vacia")
    @Size(min = 2, max = 100, message = "La marca debe tener entre 2 y 100 caracteres")
    private String brand;
    
    @NotBlank(message = "El modelo no puede estar vacio")
    @Size(min = 2, max = 100, message = "El modelo debe tener entre 2 y 100 caracteres")
    private String model;
    
    // ===== STOCK =====
    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 999999, message = "El stock no puede exceder 999999")
    private Integer stock;
    
    @NotNull(message = "El estado de disponibilidad no puede ser nulo")
    private Boolean isAvailable;
    
    // ===== CONDICIÓN =====
    @NotNull(message = "El estado de nuevo/usado no puede ser nulo")
    private Boolean isNew;
    
    @NotBlank(message = "La condición no puede estar vacia")
    @Pattern(regexp = "^(Nueva|Usado|Recondicionado)$", message = "La condición debe ser: Nueva, Usado o Recondicionado")
    private String condition;
    
    // ===== CALIFICACIÓN =====
    @DecimalMin(value = "0", message = "La calificación no puede ser menor a 0")
    @DecimalMax(value = "5", message = "La calificación no puede ser mayor a 5")
    private BigDecimal rating;
    
    @Min(value = 0, message = "El número de reseñas no puede ser negativo")
    private Integer reviewCount;
    
    // ===== VENDEDOR =====
    @NotNull(message = "El ID del vendedor no puede ser nulo")
    @Positive(message = "El ID del vendedor debe ser positivo")
    private Long vendorId;
    
    @NotBlank(message = "El nombre del vendedor no puede estar vacio")
    @Size(min = 2, max = 255, message = "El nombre del vendedor debe tener entre 2 y 255 caracteres")
    private String vendorName;
    
    @DecimalMin(value = "0", message = "La calificación del vendedor no puede ser menor a 0")
    @DecimalMax(value = "5", message = "La calificación del vendedor no puede ser mayor a 5")
    private BigDecimal vendorRating;
    
    @Min(value = 0, message = "El número de reseñas del vendedor no puede ser negativo")
    private Integer vendorReviewCount;
    
    // ===== MEDIOS DE PAGO =====
    @NotEmpty(message = "Debe haber al menos un medio de pago")
    @Size(max = 10, message = "No pueden haber más de 10 medios de pago")
    private List<@NotBlank(message = "El medio de pago no puede estar vacio") String> paymentMethods;
    
    // ===== IMÁGENES =====
    @NotEmpty(message = "Debe haber al menos una imagen")
    @Size(max = 20, message = "No pueden haber más de 20 imágenes")
    private List<@NotBlank(message = "La URL de imagen no puede estar vacia") String> imageUrls;
    
    @NotBlank(message = "La imagen principal no puede estar vacia")
    private String mainImageUrl;
    
    // ===== INFORMACIÓN ADICIONAL =====
    @Pattern(regexp = "^(ACTIVO|INACTIVO|SUSPENDIDO)$", message = "El status debe ser: ACTIVO, INACTIVO o SUSPENDIDO")
    private String status;
    
    private Boolean hasWarranty;
    
    @Size(max = 500, message = "La información de garantía debe tener máximo 500 caracteres")
    private String warrantyInfo;
    
    private Boolean isFreeShipping;
    
    @DecimalMin(value = "0", message = "El costo de envío no puede ser negativo")
    @DecimalMax(value = "99999.99", message = "El costo de envío no puede exceder 99999.99")
    private Double shippingCost;
    
    @Min(value = 1, message = "Los días de envío estimado deben ser al menos 1")
    @Max(value = 365, message = "Los días de envío estimado no pueden exceder 365")
    private Integer estimatedShippingDays;
    
    @Size(max = 50, message = "El SKU debe tener máximo 50 caracteres")
    private String sku;
}

// ===== DTOs Adicionales =====

@Data
@NoArgsConstructor
@AllArgsConstructor
class ModelDetailsRequest {
    @NotNull(message = "El ID no puede ser nulo")
    @Positive(message = "El ID debe ser un número positivo")
    private Long id;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ModelDeleteRequest {
    @NotNull(message = "El ID no puede ser nulo")
    @Positive(message = "El ID debe ser un número positivo")
    private Long id;
}

/**
 * DTO para respuestas consistentes de operaciones
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
class OperationResponse {
    private boolean success;
    private String message;
    private Long id;
    private LocalDateTime timestamp;
    private String errorCode;
}
