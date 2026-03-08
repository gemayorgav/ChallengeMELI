package com.hackerrank.sample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para crear/actualizar modelos con todos los campos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelOperationRequest {
    
    // Básico
    private Long id;
    private String name;
    private String description;
    private String category;
    
    // Precios
    private BigDecimal price;
    private BigDecimal discountPercentage;
    private Integer installmentMonths;
    
    // Características
    private String color;
    private String size;
    private String material;
    private String brand;
    private String model;
    
    // Stock
    private Integer stock;
    private Boolean isAvailable;
    
    // Condición
    private Boolean isNew;
    private String condition;
    
    // Calificación
    private BigDecimal rating;
    private Integer reviewCount;
    
    // Vendedor
    private Long vendorId;
    private String vendorName;
    private BigDecimal vendorRating;
    private Integer vendorReviewCount;
    
    // Medios de pago
    private List<String> paymentMethods;
    
    // Imágenes
    private List<String> imageUrls;
    private String mainImageUrl;
    
    // Información adicional
    private String status;
    private Boolean hasWarranty;
    private String warrantyInfo;
    private Boolean isFreeShipping;
    private Double shippingCost;
    private Integer estimatedShippingDays;
    private String sku;
}
class ModelDetailsRequest {
    private Long id;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ModelDeleteRequest {
    private Long id;
}

/**
 * DTO para respuestas de operación
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class OperationResponse {
    private boolean success;
    private String message;
    private Long id;
    private String timestamp;
}
