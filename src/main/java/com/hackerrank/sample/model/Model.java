package com.hackerrank.sample.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de producto similar a Mercado Libre
 * Representa un item completo con toda la información pertinente
 */
@Entity
@Table(name = "MODEL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Model implements Serializable {
    
    @Id
    private Long id;
    
    // ===== INFORMACIÓN BÁSICA =====
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @Column(length = 100)
    private String category;
    
    // ===== PRECIOS Y OFERTAS =====
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discountedPrice;
    
    @Column
    private Integer installmentMonths;
    
    // ===== CARACTERÍSTICAS DEL PRODUCTO =====
    @Column(length = 50)
    private String color;
    
    @Column(length = 50)
    private String size;
    
    @Column(length = 100)
    private String material;
    
    @Column(length = 50)
    private String brand;
    
    @Column(length = 30)
    private String model;
    
    // ===== STOCK Y DISPONIBILIDAD =====
    @Column(nullable = false)
    private Integer stock;
    
    @Column(nullable = false)
    private Boolean isAvailable = true;
    
    // ===== CONDICIÓN =====
    @Column(nullable = false)
    private Boolean isNew = true;
    
    @Column(length = 20)
    private String condition;
    
    // ===== CALIFICACIÓN =====
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;
    
    @Column
    private Integer reviewCount;
    
    // ===== INFORMACIÓN DEL VENDEDOR =====
    @Column(nullable = false)
    private Long vendorId;
    
    @Column(length = 100)
    private String vendorName;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal vendorRating;
    
    @Column
    private Integer vendorReviewCount;
    
    // ===== MEDIOS DE PAGO =====
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "MODEL_PAYMENT_METHODS", joinColumns = @JoinColumn(name = "model_id"))
    @Column(name = "payment_method")
    private List<String> paymentMethods = new ArrayList<>();
    
    // ===== IMÁGENES =====
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "MODEL_IMAGES", joinColumns = @JoinColumn(name = "model_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();
    
    @Column(length = 500)
    private String mainImageUrl;
    
    // ===== METADATA =====
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime publishedAt;
    
    @Column(length = 20)
    private String status;
    
    // ===== INFORMACIÓN ADICIONAL =====
    @Column
    private Boolean hasWarranty;
    
    @Column(length = 100)
    private String warrantyInfo;
    
    @Column
    private Boolean isFreeShipping;
    
    @Column
    private Double shippingCost;
    
    @Column
    private Integer estimatedShippingDays;
    
    @Column(length = 500)
    private String sku;
    
    // ===== PRE-PERSISTENCIA =====
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (stock == null) stock = 0;
        if (isAvailable == null) isAvailable = true;
        if (isNew == null) isNew = true;
        if (condition == null) condition = "NEW";
        if (rating == null) rating = BigDecimal.ZERO;
        if (reviewCount == null) reviewCount = 0;
        if (paymentMethods == null) paymentMethods = new ArrayList<>();
        if (imageUrls == null) imageUrls = new ArrayList<>();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
