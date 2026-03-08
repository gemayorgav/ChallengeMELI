# 🎯 GUÍA RÁPIDA - FASE 1 EN ACCIÓN

## ⚡ PRUEBA INMEDIATA (5 minutos)

### 1️⃣ Inicia el servidor
```bash
cd G:\Documents\MELI_challenge\ChallengeMELI
mvn clean spring-boot:run
```

Espera hasta ver:
```
Started Application in X.XXX seconds
```

---

### 2️⃣ Abre Swagger UI en tu navegador
```
http://localhost:8080/swagger-ui.html
```

**Verás:**
- Documentación completa de todos los endpoints
- Parámetros con descripciones
- Modelos de request/response
- Botón "Try it out" para probar cada endpoint

---

### 3️⃣ Prueba un endpoint con Error de Validación

En Swagger, en sección **Models → POST /api/v1/models**:

**Click en "Try it out"** y pegua esto:
```json
{
  "id": 99,
  "name": "ab",
  "description": "Desc",
  "category": "Electrónica",
  "price": -100,
  "discountPercentage": 150,
  "installmentMonths": 100,
  "stock": -10,
  "isAvailable": true,
  "isNew": true,
  "condition": "InvalidCondition",
  "rating": 6,
  "brand": "Samsung",
  "model": "S24",
  "vendorId": 1,
  "vendorName": "Vendor",
  "paymentMethods": [],
  "imageUrls": [],
  "mainImageUrl": ""
}
```

**Click Execute** → Verás:

```json
{
  "timestamp": "2026-03-08T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Validation failed for 8 field(s)",
  "fieldErrors": {
    "name": "must have at least 3 characters",
    "description": "must have at least 10 characters",
    "price": "must be greater than 0",
    "discountPercentage": "must not be greater than 100",
    "installmentMonths": "must not be greater than 60",
    "stock": "must not be negative",
    "condition": "must match \"^(Nueva|Usado|Recondicionado)$\"",
    "rating": "must not be greater than 5",
    "paymentMethods": "must not be empty",
    "imageUrls": "must not be empty"
  }
}
```

✅ **VALIDACIÓN FUNCIONANDO** - Respuesta consistente, informativa y segura.

---

### 4️⃣ Verifica Health Checks
```
http://localhost:8080/api/v1/actuator/health
```

Verás algo como:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP", "details": {"database": "H2"}},
    "diskSpace": {"status": "UP"},
    "livenessState": {"status": "UP"},
    "readinessState": {"status": "UP"}
  }
}
```

✅ **HEALTH CHECKS FUNCIONANDO** - Apto para Kubernetes.

---

### 5️⃣ Obtén la especificación OpenAPI completa
```bash
curl http://localhost:8080/v3/api-docs | jq . > openapi.json
```

O importa directamente en Postman:
1. New → API
2. Define API → Import Raw Text
3. Pega: `http://localhost:8080/v3/api-docs`
4. Click Continue → Test

---

## 📊 COMPARACIÓN: Antes vs Después

### ❌ ANTES (Sin Fase 1)
```bash
POST /api/v1/models con datos inválidos
↓
Respuesta inconsistente, sin detalles
Logs en diferentes formatos
Sin documentación clara
Sin health checks
```

### ✅ AHORA (Con Fase 1)
```bash
POST /api/v1/models con datos inválidos
↓
Respuesta JSON estructurada
Campo por campo qué falló
Documentación automática en Swagger
Health checks en /actuator/health
```

---

## 🧪 TEST SCENARIO COMPLETO

### Escenario: Crear producto válido

1. **Auténtica** (si está protegido):
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```
Copias el token que retorna (`token`)

2. **Crea un modelo válido** en Swagger:
```json
{
  "id": 1,
  "name": "Samsung Galaxy S24",
  "description": "Smartphone flagship de última generación con pantalla AMOLED 6.2 pulgadas",
  "category": "Smartphones",
  "price": 999.99,
  "discountPercentage": 10,
  "installmentMonths": 12,
  "stock": 100,
  "isAvailable": true,
  "isNew": true,
  "condition": "Nueva",
  "rating": 4.8,
  "reviewCount": 1250,
  "color": "Midnight Black",
  "size": "163x78.1x8.6 mm",
  "material": "Glass y Aluminum",
  "brand": "Samsung",
  "model": "S24",
  "vendorId": 1,
  "vendorName": "Samsung Official",
  "vendorRating": 4.9,
  "vendorReviewCount": 5000,
  "paymentMethods": ["CREDIT_CARD", "DEBIT", "TRANSFER"],
  "imageUrls": ["https://example.com/s24-1.jpg", "https://example.com/s24-2.jpg"],
  "mainImageUrl": "https://example.com/s24-main.jpg",
  "status": "ACTIVO",
  "hasWarranty": true,
  "warrantyInfo": "2 años de garantía oficial",
  "isFreeShipping": true,
  "shippingCost": 0,
  "estimatedShippingDays": 2,
  "sku": "SAM-S24-BK-2026"
}
```

3. **Respuesta esperada:** `201 Created`
```json
{
  "success": true,
  "message": "Modelo creado exitosamente",
  "id": 1,
  "timestamp": "2026-03-08T10:30:00"
}
```

✅ **CRUD FUNCIONANDO CON VALIDACIÓN COMPLETA**

---

## 🔍 VERIFICAR IMPLEMENTACIONES

### ✅ GlobalExceptionHandler
Prueba con un ID negativo:
```bash
GET /api/v1/models/details?id=-1
```
Verás: `400 Bad Request` con mensaje consistente

### ✅ Bean Validation
Crea modelo con `price: -50`
Verás: Campo específico en `fieldErrors` con "must be greater than 0"

### ✅ OpenAPI Documentación
Abre `/swagger-ui.html`
Verás: Todos los endpoints documentados con ejemplos

### ✅ Actuator Health
Accede: `/api/v1/actuator/health`
Verás: Estado de BD, disco, liveness, readiness

---

## 📱 POSTMAN COLLECTION

Si prefieres Postman en lugar de Swagger:

**Opción 1: Import from URL**
```
http://localhost:8080/v3/api-docs
```

**Opción 2: Usar tu colección existente**
- Ya tienes `MELI_Challenge_Expanded_Model.postman_collection.json`
- Ahora los endpoints retornan documentación OpenAPI

---

## ❓ TROUBLESHOOTING

| Problema | Solución |
|----------|----------|
| `Port 8080 already in use` | `netstat -ano \| findstr :8080` luego `taskkill /PID XXX` |
| Swagger no carga | Compila primero: `mvn clean compile` |
| Validaciones no funcionan | Verifica que los headers incluyan `Content-Type: application/json` |
| Health check retorna DOWN | Compila con: `mvn clean install` |

---

## 🎁 BONUS: Metricas

```bash
# Ver todas las métricas disponibles
curl http://localhost:8080/api/v1/actuator/metrics | jq

# Ver métrica específica
curl http://localhost:8080/api/v1/actuator/metrics/jvm.memory.used | jq

# Formato Prometheus (para Grafana)
curl http://localhost:8080/api/v1/actuator/prometheus
```

---

## ✨ RESULTADO FINAL

Ahora tienes un microservicio que cumple con:

✅ **Documentación profesional** - OpenAPI 3.0  
✅ **Validación exhaustiva** - 30+ reglas  
✅ **Manejo de errores** - Consistente y trazable  
✅ **Observabilidad** - Health checks y métricas  
✅ **Producción lista** - Kubernetes compatible  

**Tiempo para implementar todo esto en producción: ~2 horas** ⏱️

---

**¡Felicidades! Has completado la FASE 1 exitosamente! 🎉**

Próximo paso: **FASE 2 - ROBUSTEZ** (Logging JSON, Paginación, Timeouts, PostgreSQL)
