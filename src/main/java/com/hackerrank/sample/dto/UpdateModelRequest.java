package com.hackerrank.sample.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para actualizar modelos (PARCIAL).
 * Todos los campos son opcionales - solo se actualizan los que se envíen.
 * Sin validaciones estrictas de @NotNull/@NotBlank.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateModelRequest {
    
    @NotNull(message = "El ID del modelo es requerido")
    @Positive(message = "El ID debe ser un número positivo")
    private Long id;
    
    // ===== BÁSICO (OPCIONAL) =====
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres")
    private String name;
    
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String description;
    
    @Size(min = 2, max = 100, message = "La categoría debe tener entre 2 y 100 caracteres")
    private String category;
    
    // ===== PRECIOS (OPCIONAL) =====
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999999.99", message = "El precio no puede exceder 999999999.99")
    private BigDecimal price;
    
    @DecimalMin(value = "0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100", message = "El descuento no puede ser mayor a 100%")
    private BigDecimal discountPercentage;
    
    @Min(value = 1, message = "Las cuotas deben ser al menos 1")
    @Max(value = 60, message = "Las cuotas no pueden ser más de 60")
    private Integer installmentMonths;
    
    // ===== CARACTERÍSTICAS (OPCIONAL) =====
    @Size(max = 50, message = "El color debe tener máximo 50 caracteres")
    private String color;
    
    @Size(max = 50, message = "El tamaño debe tener máximo 50 caracteres")
    private String size;
    
    @Size(max = 50, message = "El material debe tener máximo 50 caracteres")
    private String material;
    
    @Size(min = 2, max = 100, message = "La marca debe tener entre 2 y 100 caracteres")
    private String brand;
    
    @Size(min = 2, max = 100, message = "El modelo debe tener entre 2 y 100 caracteres")
    private String model;
    
    // ===== STOCK (OPCIONAL) =====
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 999999, message = "El stock no puede exceder 999999")
    private Integer stock;
    
    private Boolean isAvailable;
    
    // ===== CONDICIÓN (OPCIONAL) =====
    private Boolean isNew;
    
    @Pattern(regexp = "^(Nueva|Usado|Recondicionado)$", message = "La condición debe ser: Nueva, Usado o Recondicionado")
    private String condition;
    
    // ===== CALIFICACIÓN (OPCIONAL) =====
    @DecimalMin(value = "0", message = "La calificación no puede ser menor a 0")
    @DecimalMax(value = "5", message = "La calificación no puede ser mayor a 5")
    private BigDecimal rating;
    
    @Min(value = 0, message = "El número de reseñas no puede ser negativo")
    private Integer reviewCount;
    
    // ===== VENDEDOR (OPCIONAL) =====
    @Positive(message = "El ID del vendedor debe ser un número positivo")
    private Long vendorId;
    
    @Size(max = 100, message = "El nombre del vendedor debe tener máximo 100 caracteres")
    private String vendorName;
    
    @DecimalMin(value = "0", message = "La calificación del vendedor no puede ser negativa")
    @DecimalMax(value = "5", message = "La calificación del vendedor no puede ser mayor a 5")
    private BigDecimal vendorRating;
    
    @Min(value = 0, message = "El número de reseñas del vendedor no puede ser negativo")
    private Integer vendorReviewCount;
    
    // ===== MEDIOS DE PAGO (OPCIONAL) =====
    private List<String> paymentMethods;
    
    // ===== IMÁGENES (OPCIONAL) =====
    private List<String> imageUrls;
    
    @Size(max = 500, message = "La URL de imagen principal debe tener máximo 500 caracteres")
    private String mainImageUrl;
    
    // ===== INFORMACIÓN ADICIONAL (OPCIONAL) =====
    @Pattern(regexp = "^(ACTIVO|INACTIVO|SUSPENDIDO)$", message = "El estado debe ser: ACTIVO, INACTIVO o SUSPENDIDO")
    private String status;
    
    private Boolean hasWarranty;
    
    @Size(max = 500, message = "La información de garantía debe tener máximo 500 caracteres")
    private String warrantyInfo;
    
    private Boolean isFreeShipping;
    
    @DecimalMin(value = "0", message = "El costo de envío no puede ser negativo")
    private Double shippingCost;
    
    @Min(value = 1, message = "Los días de envío estimados deben ser al menos 1")
    @Max(value = 365, message = "Los días de envío estimados no pueden exceder 365")
    private Integer estimatedShippingDays;
    
    @Size(max = 100, message = "El SKU debe tener máximo 100 caracteres")
    private String sku;
}
