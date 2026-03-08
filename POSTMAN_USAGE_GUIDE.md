# 📬 Colecciones Postman - Guía de Uso

## Dos Versiones Disponibles

### **MELI_Challenge_PHASE2_UPDATED.postman_collection.json** ✅ RECOMENDADA
- **Estado**: Actualizada para Phase 2
- **Contenido**: Íntegro, completo y moderno
- **Características**:
  - ✅ Section 1: Autenticación (testuser/admin con token auto-save)
  - ✅ Section 2: Documentación & Salud (Swagger, OpenAPI, Health checks)
  - ✅ Section 3: CRUD Operaciones (Crear, Leer, Actualizar, Eliminar)
  - ✅ Section 4: **Paginación - PHASE 2** (4 ejemplos con diferentes sorts)
  - ✅ Section 5: Consultas Individuales (GET /models/details)
  - ✅ Section 6: Actualizar & Eliminar (PUT, DELETE)
  - ✅ Section 7: Error Handling (Validaciones, 404s, etc.)
- **Endpoints totales**: 20+
- **Ejemplos de paginación**:
  - Página 0 con 10 elementos
  - Ordenado por precio (descendente)
  - Orden por rating (top 5)
  - Orden por nombre (A-Z)
  - Navegación entre páginas
- **Requerimientos**:
  - Token en header (auto-guardado desde login)
  - Validaciones: condition, status, price, stock, rating
- **Compatible**: Phase 1 + Phase 2

---

### **MELI_Challenge_PHASE1_UPDATED.postman_collection.json** (Legada)
- **Estado**: Actualizada pero versión anterior
- **Nota**: Usar solo si necesitas versión compatible Phase 1 exclusivamente
- **Diferencia**: No tiene ejemplos de paginación avanzada

---

## 🚀 Cómo Usar en Postman

### 1. **Importar Colección**
```
File > Import > Selecciona MELI_Challenge_PHASE2_UPDATED.postman_collection.json
```

### 2. **Configurar Base URL** (Opcional)
La variable `base_url` viene preconfigurada en `http://localhost:8080`
Si usas otro puerto, cambia la variable en:
- Colección > Variables > base_url

### 3. **Flujo de Testing Recomendado**

#### **Paso 1: Autenticación**
```
1. AUTENTICACION > 1.1 Login - testuser
  → Guarda token automáticamente
```

#### **Paso 2: Verificar Salud**
```
2. DOCUMENTACION & SALUD > 2.3 Health Check - Completo
  → Verifica que el servicio esté UP
```

#### **Paso 3: Llenar BD**
```
3. CRUD OPERATIONS
  → 3.1 Limpiar BD (opcional, solo si quieres empezar limpio)
  → 3.2 Crear - iPhone 15 Pro Max
  → 3.3 Crear - Samsung Galaxy S24
  → 3.4 Crear - Producto Usado
```

#### **Paso 4: Probar Paginación** ⭐ **PHASE 2**
```
4. LISTADO - PAGINACION (PHASE 2)
  → 4.1 Listar - Página 0 (10 elementos)
  → 4.2 Listar - Ordenado por precio (desc)
  → 4.3 Listar - Ordenado por rating (desc)
  → 4.4 Listar - Ordenado por nombre (asc)
  → 4.5 Listar - Página 1 (siguiente página)
```

#### **Paso 5: Operaciones Individual**
```
5. CONSULTAS INDIVIDUALES
  → 5.1 Obtener Detalles - ID 1
  → 5.2 Obtener Detalles - ID 2
```

#### **Paso 6: Actualizar**
```
6. ACTUALIZAR & ELIMINAR
  → 6.1 Actualizar - Cambiar precio
  → 6.2 Actualizar - Cambiar stock y rating
  → 6.3 Actualizar - Cambiar estado a SUSPENDIDO
```

#### **Paso 7: Verificar Errores**
```
7. ERROR HANDLING
  → 7.1 Error - Condition inválida (400)
  → 7.2 Error - Status inválido (400)
  → 7.3 Error - Not Found (404)
  → 7.4 Error - Missing required fields (400)
```

---

## 📊 Parámetros de Paginación (PHASE 2)

### Estructura de GET /models
```
GET /api/v1/models?page=0&size=10&sort=id&direction=asc
```

| Parámetro | Default | Descripción | Ejemplo |
|-----------|---------|-------------|---------|
| `page` | 0 | Número de página (0-based) | `?page=1` → segunda página |
| `size` | 10 | Elementos por página | `?size=20` → 20 elementos |
| `sort` | id | Campo para ordenar | `?sort=price` → por precio |
| `direction` | asc | asc (↑) o desc (↓) | `?direction=desc` → mayor a menor |

### Ejemplos Prácticos

**Listar todo (primeros 10):**
```
GET /api/v1/models?page=0&size=10&sort=id&direction=asc
```

**Top 5 mejor calificados:**
```
GET /api/v1/models?page=0&size=5&sort=rating&direction=desc
```

**Productos más caros:**
```
GET /api/v1/models?page=0&size=20&sort=price&direction=desc
```

**Alfabético A-Z:**
```
GET /api/v1/models?page=0&size=10&sort=name&direction=asc
```

**Segunda página de resultados:**
```
GET /api/v1/models?page=1&size=10&sort=id&direction=asc
```

---

## ✅ Valores Validados

### Condition (campo requerido)
```
"Nueva" | "Usado" | "Recondicionado"
```

### Status (campo requerido)
```
"ACTIVO" | "INACTIVO" | "SUSPENDIDO"
```

### Price (campo requerido)
```
Cualquier número > 0
Ej: 100.50, 1299.99
```

### Stock (campo requerido)
```
Entero >= 0
Ej: 10, 45, 0
```

### Rating (campo requerido)
```
Decimal: 0 - 5
Ej: 4.5, 4.8, 5.0
```

---

## 🔒 Autenticación

Todos los endpoints (excepto documentación y health) requieren JWT token Bearer.

**Headers automáticos:**
```json
{
  "Authorization": "Bearer {{token}}"
}
```

El token se guarda automáticamente de los login en la sección **1. AUTENTICACION**.

---

## 📝 Response Examples

### Paginación Success (200)
```json
{
  "success": true,
  "data": [
    { "id": 1, "name": "iPhone 15 Pro Max", "price": 1199.99, ... },
    { "id": 2, "name": "Samsung Galaxy S24", "price": 1299.99, ... }
  ],
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalElements": 150,
    "totalPages": 15,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2026-03-08T02:30:00"
}
```

### Validation Error (400)
```json
{
  "timestamp": "2026-03-08T02:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "condition": "must be one of: Nueva, Usado, Recondicionado"
  }
}
```

### Not Found Error (404)
```json
{
  "timestamp": "2026-03-08T02:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "No model with given id found."
}
```

---

## 🛠️ Tips de Uso

1. **Auto-complete de token**: Ejecuta cualquiera de los logins (1.1 o 1.2) primero
2. **Limpiar BD**: Usa 3.1 si quieres empezar desde cero
3. **Variables locales**: Puedes crear variables adicionales en Postman > Environments
4. **Pre-scripts**: Los requests login tienen scripts que guardan el token automáticamente
5. **Copiar cURL**: Botón derecho > Copy as cURL para usar en terminal

---

## 📋 Checklist de Testing

- [ ] Login exitoso (token guardado)
- [ ] Health check OK (status UP)
- [ ] Crear producto (201 Created)
- [ ] Listar paginado (200 OK con metadata)
- [ ] Ordenamiento funciona (by price, rating, name, etc)
- [ ] Obtener detalles (200 OK)
- [ ] Actualizar campo (200 OK)
- [ ] Error validación devuelve 400
- [ ] 404 para ID inexistente
- [ ] Eliminar funciona (204 No Content)

---

## 🔄 Flujo Completo (5 minutos)

```bash
# 1. Login
1.1 Login - testuser
    ↓
# 2. Verificar Salud
2.3 Health Check - Completo
    ↓
# 3. Llenar Datos
3.2 Crear - iPhone (201)
3.3 Crear - Samsung (201)
3.4 Crear - MacBook Usado (201)
    ↓
# 4. Listar Paginado
4.1 Página 0 (10 elementos)
4.2 Por precio DESC
4.3 Por rating DESC (Top 5)
    ↓
# 5. Detalles
5.1 ID 1
    ↓
# 6. Actualizar
6.1 Cambiar precio
    ↓
# 7. Validación
7.1 Condition inválida (400)
    ↓
# 8. Limpiar
3.1 DELETE /models/erase
```

---

**Versión**: Phase 2 (Essentials + Robustez)  
**Última actualización**: 2026-03-08  
**Estado**: ✅ Productions Ready
