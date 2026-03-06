# 📋 GUÍA COMPLETA DE TESTING - Modelo Expandido

## 🎯 Introducción

Esta guía te instruye cómo realizar pruebas **completas** del modelo de producto expandido con **40+ campos** similar a Mercado Libre usando **Postman**.

**Fecha:** 5 de Marzo 2026
**Versión:** 3.0 - Modelo Expandido
**Colección:** `MELI_Challenge_Expanded_Model.postman_collection.json`

---

## 📦 Instalación en Postman

### Opción 1: Importar desde archivo
1. Abre **Postman**
2. Haz clic en **Import** (esquina superior izquierda)
3. Selecciona **Upload Files**
4. Busca y selecciona: `MELI_Challenge_Expanded_Model.postman_collection.json`
5. Haz clic en **Import**

### Opción 2: Copiar/pegar desde archivo
1. Abre `MELI_Challenge_Expanded_Model.postman_collection.json`
2. Copia TODO el contenido JSON
3. En Postman: Import → Paste Raw Text
4. Pega el contenido y haz clic en **Continue** → **Import**

---

## ⚙️ Configuración Inicial

### Variables de Entorno (Postman)
La colección usa variables globales que debes configurar:

**1. Abre: Postman → Settings → Variables**

**2. Agrega una variable global:**
| Variable | Valor |
|----------|-------|
| `base_url` | `http://localhost:8080` |
| `token` | (se llena después del login) |

**3. También puedes usar la variable del entorno:** (recomendado)
- En la colección, verás `{{base_url}}` y `{{token}}`
- Éstos se reemplazan automáticamente

---

## ✅ PASO A PASO: Flujo Completo de Testing

### ⏱️ Tiempo estimado: 10-15 minutos

---

### **PASO 1️⃣ : AUTENTICARSE (LOGIN)**

**Endpoint:** `POST /auth/login`

**Request:**
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**Pasos en Postman:**
1. Abre la carpeta **"1️⃣ AUTENTICACIÓN JWT"**
2. Selecciona **"1.1 Login - testuser"**
3. Haz clic en **Send**

**Resultado esperado (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTczNDAxODExOCwiZXhwIjoxNzM0MDIxNzE4fQ.X5Zm...",
  "username": "testuser",
  "email": "testuser@example.com",
  "role": "USER"
}
```

**⚠️ IMPORTANTE:**
1. Copia el valor de `token` (el JWT completo)
2. En Postman, ve a **Variables** (pestaña superior)
3. En `Current value` de `token`, pega el JWT completo
4. Haz clic en **Save** (Ctrl+S)

---

### **PASO 2️⃣ : LIMPIAR BASE DE DATOS**

**Endpoint:** `DELETE /api/v1/models/erase`

**Pasos:**
1. Abre **"2️⃣ GESTIÓN DE BASE DE DATOS"**
2. Selecciona **"2.1 Limpiar BD - DELETE /models/erase"**
3. Verifica que el header `Authorization: Bearer {{token}}` esté presente
4. Haz clic en **Send**

**Resultado esperado (204 No Content):**
```
Body: (vacío)
Status: 204
```

**✅ Confirmación:** La base de datos está ahora limpia, sin productos.

---

### **PASO 3️⃣ : CREAR PRIMER PRODUCTO (iPhone 15 Pro Max)**

**Endpoint:** `POST /api/v1/models`

**Pasos:**
1. Abre **"3️⃣ CREAR PRODUCTOS (POST /models)"**
2. Selecciona **"3.1 iPhone 15 Pro Max (Completo)"**
3. Revisa el body JSON (contiene todos los 40+ campos)
4. Haz clic en **Send**

**Campos que se envían:**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro Max",
  "description": "...",
  "category": "Electrónica/Celulares/Smartphones Premium",
  "price": 1199.99,
  "discountPercentage": 10,
  "discountedPrice": 1079.99,
  "installmentMonths": 12,
  "color": "Titanio Negro",
  "size": "6.7 pulgadas",
  "material": "Titanio quirúrgico, Ceramic Shield, Corning Gorilla Glass",
  "brand": "Apple",
  "model": "iPhone 15 Pro Max",
  "stock": 45,
  "isAvailable": true,
  "isNew": true,
  "condition": "NEW",
  "rating": 4.8,
  "reviewCount": 2450,
  "vendorId": 1,
  "vendorName": "Apple Premium Store Oficial",
  "vendorRating": 4.9,
  "vendorReviewCount": 12500,
  "paymentMethods": ["CREDIT_CARD", "DEBIT_CARD", "MERCADO_PAGO", "CRYPTO_PAYMENT", "INSTALLMENT_PLAN"],
  "imageUrls": ["https://s3.../image1.jpg", "https://s3.../image2.jpg", ...],
  "mainImageUrl": "https://s3.../main.jpg",
  "hasWarranty": true,
  "warrantyInfo": "Garantía internacional de 2 años + AppleCare+",
  "isFreeShipping": true,
  "shippingCost": 0,
  "estimatedShippingDays": 2,
  "sku": "APPLE-IP15PM-TI-256GB-2026",
  "status": "ACTIVE"
}
```

**Resultado esperado (201 Created):**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro Max",
  "price": 1199.99,
  "stock": 45,
  "status": "ACTIVE",
  "createdAt": "2026-03-05T20:55:12.123456",
  "updatedAt": "2026-03-05T20:55:12.123456",
  ... (todos los 40+ campos)
}
```

**✅ Confirmación:** Producto iPhone creado exitosamente con ID=1

---

### **PASO 4️⃣ : CREAR SEGUNDO PRODUCTO (Samsung Galaxy S24 Ultra)**

**Endpoint:** `POST /api/v1/models`

**Pasos:**
1. Abre **"3️⃣ CREAR PRODUCTOS"**
2. Selecciona **"3.2 Samsung Galaxy S24 Ultra"**
3. Haz clic en **Send**

**Resultado esperado (201 Created):**
Producto Samsung creado con ID=2

**✅ Confirmación:** Ahora tienes 2 productos en BD

---

### **PASO 5️⃣ : LISTAR TODOS LOS PRODUCTOS**

**Endpoint:** `GET /api/v1/models`

**Pasos:**
1. Abre **"4️⃣ LISTAR PRODUCTOS (GET /models)"**
2. Selecciona **"4.1 Listar todos los productos"**
3. Haz clic en **Send**

**Resultado esperado (200 OK):**
```json
[
  {
    "id": 1,
    "name": "iPhone 15 Pro Max",
    "price": 1199.99,
    "stock": 45,
    "status": "ACTIVE"
  },
  {
    "id": 2,
    "name": "Samsung Galaxy S24 Ultra",
    "price": 1299.99,
    "stock": 32,
    "status": "ACTIVE"
  }
]
```

**✅ Confirmación:** Ambos productos aparecen en la lista

---

### **PASO 6️⃣ : OBTENER DETALLES COMPLETOS DE UN PRODUCTO**

**Endpoint:** `GET /api/v1/models/details`

**Pasos:**
1. Abre **"5️⃣ OBTENER DETALLES (GET /models/details)"**
2. Selecciona **"5.1 Obtener detalles producto ID 1"**
3. El body contiene: `{ "id": 1 }`
4. Haz clic en **Send**

**Resultado esperado (200 OK):**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro Max",
  "description": "Premium smartphone con cámara de 48MP...",
  "category": "Electrónica/Celulares/Smartphones Premium",
  "price": 1199.99,
  "discountPercentage": 10,
  "discountedPrice": 1079.99,
  "installmentMonths": 12,
  "color": "Titanio Negro",
  "size": "6.7 pulgadas",
  "material": "Titanio quirúrgico, Ceramic Shield, Corning Gorilla Glass",
  "brand": "Apple",
  "model": "iPhone 15 Pro Max",
  "stock": 45,
  "isAvailable": true,
  "isNew": true,
  "condition": "NEW",
  "rating": 4.8,
  "reviewCount": 2450,
  "vendorId": 1,
  "vendorName": "Apple Premium Store Oficial",
  "vendorRating": 4.9,
  "vendorReviewCount": 12500,
  "paymentMethods": ["CREDIT_CARD", "DEBIT_CARD", "MERCADO_PAGO", "CRYPTO_PAYMENT", "INSTALLMENT_PLAN"],
  "imageUrls": [
    "https://s3.amazonaws.com/meli-images/iphone15-pro-max-1.jpg",
    "https://s3.amazonaws.com/meli-images/iphone15-pro-max-2.jpg",
    "https://s3.amazonaws.com/meli-images/iphone15-pro-max-3.jpg",
    "https://s3.amazonaws.com/meli-images/iphone15-pro-max-4.jpg"
  ],
  "mainImageUrl": "https://s3.amazonaws.com/meli-images/iphone15-pro-max-main.jpg",
  "hasWarranty": true,
  "warrantyInfo": "Garantía internacional de 2 años + AppleCare+",
  "isFreeShipping": true,
  "shippingCost": 0,
  "estimatedShippingDays": 2,
  "sku": "APPLE-IP15PM-TI-256GB-2026",
  "status": "ACTIVE",
  "createdAt": "2026-03-05T20:55:12.123456789",
  "updatedAt": "2026-03-05T20:55:12.123456789"
}
```

**✅ Confirmación:** Todos los 40+ campos se retornan correctamente, incluyendo:
- Arrays: `paymentMethods` (5 métodos) y `imageUrls` (4 imágenes)
- Metadata: `createdAt`, `updatedAt`
- Precios: `price`, `discountPercentage`, `discountedPrice`
- Vendor info: `vendorId`, `vendorName`, `vendorRating`, etc.

---

### **PASO 7️⃣ : ACTUALIZAR PRODUCTO**

**Endpoint:** `PUT /api/v1/models`

**Pasos:**
1. Abre **"6️⃣ ACTUALIZAR PRODUCTOS (PUT /models)"**
2. Selecciona **"6.1 Actualizar precio y stock iPhone"**
3. Body contiene:
   ```json
   {
     "id": 1,
     "name": "iPhone 15 Pro Max",
     "price": 1149.99,
     "discountPercentage": 20,
     "discountedPrice": 919.99,
     "stock": 35
   }
   ```
4. Haz clic en **Send**

**Resultado esperado (200 OK):**
```json
{
  "id": 1,
  "name": "iPhone 15 Pro Max",
  "price": 1149.99,
  "discountPercentage": 20,
  "discountedPrice": 919.99,
  "stock": 35,
  "updatedAt": "2026-03-05T20:57:45.987654321",
  ... más campos
}
```

**✅ Confirmación:** 
- Precio cambió de 1199.99 a 1149.99
- Stock cambió de 45 a 35
- `updatedAt` se actualizó automáticamente

---

### **PASO 8️⃣ : ELIMINAR PRODUCTO**

**Endpoint:** `DELETE /api/v1/models`

**Pasos:**
1. Abre **"7️⃣ ELIMINAR PRODUCTOS (DELETE /models)"**
2. Selecciona **"7.1 Eliminar producto ID 3 (Sony)"**
   - (Primero crea producto Sony usando 3.3, luego elimínalo)
3. Body: `{ "id": 3 }`
4. Haz clic en **Send**

**Resultado esperado (204 No Content):**
```
Body: (vacío)
Status: 204
```

**✅ Confirmación:** Producto eliminado exitosamente

---

### **PASO 9️⃣ : VERIFICAR ESTADO FINAL**

**Endpoint:** `GET /api/v1/models`

**Pasos:**
1. Abre **"4️⃣ LISTAR PRODUCTOS"**
2. Selecciona **"4.1 Listar todos los productos"**
3. Haz clic en **Send**

**Resultado esperado:**
```json
[
  {
    "id": 1,
    "name": "iPhone 15 Pro Max",
    "price": 1149.99,  // ← Precio actualizado
    "stock": 35        // ← Stock actualizado
  },
  {
    "id": 2,
    "name": "Samsung Galaxy S24 Ultra",
    "price": 1299.99,
    "stock": 32
  }
]
```

**✅ Confirmación:** Sony fue eliminado, quedan solo iPhone y Samsung

---

## 📊 FLUJO RECOMENDADO RÁPIDO

Si quieres una prueba **completa en 10 minutos**, sigue este orden desde la carpeta **"📝 FLUJO COMPLETO DE PRUEBA"**:

```
1. LOGIN - testuser → Obtén token y cópialo a Variables
2. LIMPIAR BD → DELETE /erase
3. CREAR iPhone → POST /models (todos 40+ campos)
4. CREAR Samsung → POST /models
5. LISTAR productos → GET /models
6. OBTENER detalles ID 1 → GET /details (verifica todos los campos)
7. ACTUALIZAR precio/stock → PUT /models
8. ELIMINAR Samsung → DELETE /models ID 2
9. LISTAR finales → GET /models (verificar que quedan 2: iPhone + Sony)
```

**Tiempo total:** ~10 minutos

---

## 🧪 PRUEBAS DE VALIDACIÓN ADICIONALES

### Test 1: Error de Autenticación
**Endpoint:** `POST /auth/login`
**Body:**
```json
{
  "username": "usuarioInvalido",
  "password": "passwordWrong"
}
```
**Resultado esperado:** 401 Unauthorized

---

### Test 2: Crear con datos parciales (Validación)
**Endpoint:** `POST /api/v1/models`
**Body:**
```json
{
  "id": 99,
  "name": "Producto Incompleto"
}
```
**Resultado esperado:** 201 Created (valores null rellenos con defaults)

---

### Test 3: Obtener producto con ID inexistente
**Endpoint:** `GET /api/v1/models/details`
**Body:**
```json
{
  "id": 9999
}
```
**Resultado esperado:** 404 Not Found

---

### Test 4: Eliminar producto con ID inexistente
**Endpoint:** `DELETE /api/v1/models`
**Body:**
```json
{
  "id": 9999
}
```
**Resultado esperado:** 404 Not Found

---

## 🗂️ ESTRUCTURA DE CAMPOS DEL MODELO

### **Información Básica** (3 campos)
- `name`: Nombre del producto (String, requerido)
- `description`: Descripción larga (String, 2000 chars máx)
- `category`: Categoría del producto (String, 100 chars máx)

### **Precios y Ofertas** (4 campos)
- `price`: Precio base (BigDecimal, ej: 1199.99)
- `discountPercentage`: Porcentaje de descuento (BigDecimal, 0-100)
- `discountedPrice`: Precio con descuento (BigDecimal)
- `installmentMonths`: Meses de financiamiento (Integer)

### **Características** (5 campos)
- `color`: Color del producto (String, 30 chars)
- `size`: Tamaño/dimensiones (String, 100 chars)
- `material`: Material principal (String, 150 chars)
- `brand`: Marca (String, 100 chars)
- `model`: Modelo específico (String, 100 chars)

### **Stock y Disponibilidad** (2 campos)
- `stock`: Cantidad en inventario (Integer)
- `isAvailable`: ¿Está disponible? (Boolean)

### **Condición** (2 campos)
- `isNew`: ¿Es nuevo? (Boolean)
- `condition`: Estado (String: NEW, LIKE_NEW, GOOD, FAIR, USED)

### **Calificación** (2 campos)
- `rating`: Calificación 0-5 (BigDecimal)
- `reviewCount`: Número de reseñas (Integer)

### **Vendedor** (4 campos)
- `vendorId`: ID del vendedor (Long)
- `vendorName`: Nombre del vendedor (String, 100 chars)
- `vendorRating`: Calificación del vendedor (Double)
- `vendorReviewCount`: Reseñas del vendedor (Integer)

### **Medios de Pago** (1 campo - Array)
- `paymentMethods`: Lista de métodos (String[])
  - Ejemplos: "CREDIT_CARD", "DEBIT_CARD", "MERCADO_PAGO", "CRYPTO_PAYMENT", "INSTALLMENT_PLAN"

### **Imágenes** (2 campos)
- `imageUrls`: Lista de URLs (String[], almacenadas en tabla auxiliar MODEL_IMAGES)
- `mainImageUrl`: Imagen principal/thumbnail (String, 500 chars)

### **Garantía y Envío** (4 campos)
- `hasWarranty`: ¿Tiene garantía? (Boolean)
- `warrantyInfo`: Detalles de garantía (String, 100 chars)
- `isFreeShipping`: ¿Envío gratis? (Boolean)
- `shippingCost`: Costo de envío (Double)
- `estimatedShippingDays`: Días estimados (Integer)

### **Metadata** (4 campos - Generados automáticamente)
- `createdAt`: Fecha de creación (LocalDateTime, auto-generado)
- `updatedAt`: Fecha de última actualización (LocalDateTime, auto-generado)
- `publishedAt`: Fecha de publicación (LocalDateTime, nullable)
- `status`: Estado (String: ACTIVE, INACTIVE, DELETED, SOLD)

### **Información Adicional** (2 campos)
- `sku`: Código SKU único (String, 500 chars)
- `id`: ID del producto (Long, requerido para identificar)

**Total: 40+ campos**

---

## 🔍 VERIFICAR ALMACENAMIENTO EN BASE DE DATOS

Para verificar que los datos se almacenan correctamente:

1. **Abre la consola del servidor** donde corre Spring Boot
2. **Busca líneas de inicialización:**
   ```
   [INIT] Usuario de prueba creado: testuser
   [INIT] Base de datos inicializada correctamente
   ```

3. **Busca logs de auditoría** de creación:
   ```
   [AUDIT] [ModelService] [USER:testuser] [POST /api/v1/models] [ACTION:CREATE] [RESULT:SUCCESS] [STATUS:201]
   [API] Usuario: testuser - Creó producto: iPhone 15 Pro Max (ID: 1) - Precio: 1199.99
   ```

4. **Verifica tablas H2** creadas automáticamente:
   ```
   MODEL (45+ columnas)
   MODEL_PAYMENT_METHODS (tabla auxiliar para arrays)
   MODEL_IMAGES (tabla auxiliar para URLs de imágenes)
   ```

---

## 📝 NOTAS IMPORTANTES

1. **JWT Token expira en 1 hora** - Si ves error 401, vuelve a hacer login (PASO 1)
2. **Base de datos es en memoria (H2)** - Se pierde al reiniciar servidor
3. **BigDecimal para precios** - Usa formato: `1199.99` (no strings)
4. **Arrays en JSON** - `paymentMethods` y `imageUrls` son arrays JSON
5. **Imágenes son URLs** - No almacenan binarios, solo referencias a buckets cloud (S3, GCS, Azure)
6. **Timestamps automáticos** - No necesitas enviar `createdAt` ni `updatedAt`

---

## 🆘 Troubleshooting

| Problema | Solución |
|----------|----------|
| 401 Unauthorized | Token expirado. Vuelve a hacer login |
| 403 Forbidden | No tienes permisos. Usa `admin`/`admin123` si es necesario |
| 400 Bad Request | JSON con formato inválido. Verifica estructura |
| 404 Not Found | Producto no existe. Verifica el ID |
| 500 Internal Server Error | Error en servidor. Revisa logs de Spring Boot |
| Variables {{token}} no se reemplazan | Configura variable global en Postman Settings |

---

## 📌 Resumen

✅ **Modelo expandido a 40+ campos**
✅ **Crear, leer, actualizar y eliminar productos**
✅ **Soporta arrays (paymentMethods, imageUrls)**
✅ **Almacenamiento en base de datos H2 con tablas auxiliares**
✅ **JWT authentication con Bearer tokens**
✅ **Audit logging de operaciones**
✅ **Resilience patterns (Circuit Breaker, Retry)**

**¡Listo para hacer pruebas completas en Postman!** 🚀
