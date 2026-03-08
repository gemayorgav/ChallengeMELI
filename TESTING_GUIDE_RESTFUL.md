# MELI Challenge - Guía de Testing v3 (RESTful Compliant)

## 📋 Cambios Importantes en v3

### ✅ Métodos HTTP Correctos (RESTful)

En la versión anterior usábamos **POST para TODO**, lo cual no es correcto. Ahora implementamos:

| Operación | Versión v2 | Versión v3 | Razón |
|-----------|-----------|-----------|-------|
| **Listar** | POST /api/v1/models/list | GET /api/v1/models | GET es para consultas, no modifica datos |
| **Crear** | POST /api/v1/models/create | POST /api/v1/models | POST crea nuevos recursos |
| **Obtener** | POST /api/v1/models/details | POST /api/v1/models/details | POST (porque necesita body con ID) |
| **Eliminar** | POST /api/v1/models/delete | DELETE /api/v1/models | DELETE es para eliminar recursos |
| **Limpiar** | POST /api/v1/models/erase | DELETE /api/v1/models/erase | DELETE es para operaciones destructivas |

### ✅ Validación de DELETE (404 cuando no existe)

**Problema anterior:** `DELETE /api/v1/models` retornaba 200 OK aunque el modelo no existía.  
**Solución:** Ahora valida que el modelo existe antes de eliminarlo:
- ✅ Si existe → DELETE retorna **200 OK** (Se eliminó)
- ✅ Si NO existe → DELETE retorna **404 Not Found** (No hay nada que eliminar)

**Ejemplo:**
```
GET /api/v1/models → [ID:1, ID:2] (2 modelos)
DELETE /api/v1/models?id=3 → 404 Not Found (No existe)
DELETE /api/v1/models?id=1 → 200 OK (Se eliminó)
GET /api/v1/models → [ID:2] (1 modelo restante)
DELETE /api/v1/models?id=1 → 404 Not Found (Ya fue eliminado)
```

---

## 🔐 Autenticación JWT

Exactamente igual que antes. Requiere tokens Bearer en header.

**Usuarios de Prueba:**
- Usuario: `testuser` | Contraseña: `password123` | Rol: `USER`
- Usuario: `admin` | Contraseña: `admin123` | Rol: `ADMIN`

---

## 🚀 Flujo de Testing Completo (v3)

### Paso 1: Obtener Token
```bash
POST /auth/login
{
  "username": "testuser",
  "password": "password123"
}
```
→ Respuesta: `{"token": "eyJ...", "username": "testuser", ...}`

### Paso 2: Guardar Token en Variable
- Copia el valor de `token` de la respuesta
- En Postman: Pestaña Variables → Pega en `{{token}}`

### Paso 3: Limpiar BD
```bash
DELETE /api/v1/models/erase
Authorization: Bearer {{token}}
```
→ Respuesta: `{"success": true, "message": "Todos los modelos han sido eliminados"}`

### Paso 4: Verificar Vacía
```bash
GET /api/v1/models
Authorization: Bearer {{token}}
```
→ Respuesta: `{"success": true, "data": []}`

### Paso 5: Crear 3 Modelos
```bash
POST /api/v1/models
Authorization: Bearer {{token}}
{
  "id": 1,
  "name": "iPhone 15"
}
```
→ Respuesta: `201 Created`

Repetir con:
- `{"id": 2, "name": "iPhone 16 Pro"}`
- `{"id": 3, "name": "Samsung Galaxy S24"}`

### Paso 6: Listar
```bash
GET /api/v1/models
Authorization: Bearer {{token}}
```
→ Respuesta: Array con 3 modelos

### Paso 7: Obtener Detalles
```bash
POST /api/v1/models/details
Authorization: Bearer {{token}}
{
  "id": 1
}
```
→ Respuesta: Objeto completo del modelo 1

### Paso 8: Eliminar Uno
```bash
DELETE /api/v1/models
Authorization: Bearer {{token}}
{
  "id": 3
}
```
→ Respuesta: `200 OK` (Se eliminó Samsung)

### Paso 9: Verificar Eliminación
```bash
GET /api/v1/models
Authorization: Bearer {{token}}
```
→ Respuesta: Array con 2 modelos (sin Samsung)

### Paso 10: Intentar Eliminar Inexistente
```bash
DELETE /api/v1/models
Authorization: Bearer {{token}}
{
  "id": 999
}
```
→ Respuesta: `404 Not Found` ✅ **¡Correcto! Validación funciona**

---

## 📚 Estándares RESTful Explicados

### GET (Lectura - Safe & Idempotent)
- **No modifica** datos en el servidor
- Puede ejecutarse múltiples veces sin cambios
- Uso: Consultas, búsquedas, listados

```
GET /api/v1/models → Obtiene todos
GET /api/v1/models?id=1 → Obtiene el modelo 1 (si soporta query params)
```

### POST (Creación - Idempotent si include id)
- **Modifica** datos creando un nuevo recurso
- Realiza la acción en el body
- Uso: Crear nuevos elementos

```
POST /api/v1/models
{
  "id": 1,
  "name": "iPhone"
}
```

### DELETE (Eliminación - Idempotent)
- **Modifica** datos eliminando un recurso
- Primera llamada: Elimina (200 OK)
- Llamadas posteriores: No existe (404 Not Found) ← **Esto es CORRECTO**
- NO es idempotent en sentido HTTP (retorna diferente), pero es semánticamente idempotent

```
DELETE /api/v1/models?id=1 → Primera vez: 200 OK
DELETE /api/v1/models?id=1 → Segunda vez: 404 Not Found (estado final = no existe)
```

### ¿Por qué **NO** usar POST para DELETE?

```
❌ INCORRECTO: POST /api/v1/models/delete {"id": 1} → 200 OK
❌ INCORRECTO: POST /api/v1/models/delete {"id": 1} → 200 OK (otra vez)

✅ CORRECTO: DELETE /api/v1/models {"id": 1} → 200 OK
✅ CORRECTO: DELETE /api/v1/models {"id": 1} → 404 Not Found (ya no existe)
```

---

## 🔄 Stack Tecnológico

- **Java 21** - LTS version
- **Spring Boot 3.2.5** - Latest Spring Boot
- **H2 Database** - Base de datos en memoria
- **JPA/Hibernate** - ORM
- **JJWT 0.12.3** - JWT token generation/validation (HS512 con 512+ bits)
- **Resilience4j 2.1.0** - Circuit Breaker & Retry
- **Lombok 1.18.30** - Reduce boilerplate
- **Maven 3.9.6** - Build tool

---

## 📝 Ejemplos con curl

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### Listar (GET - safe)
```bash
curl -X GET http://localhost:8080/api/v1/models \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json"
```

### Crear (POST - create)
```bash
curl -X POST http://localhost:8080/api/v1/models \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"iPhone 15"}'
```

### Obtener Detalles (POST - with body)
```bash
curl -X POST http://localhost:8080/api/v1/models/details \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"id":1}'
```

### Eliminar (DELETE - destructive)
```bash
curl -X DELETE http://localhost:8080/api/v1/models \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{"id":1}'
```

### Limpiar Todo (DELETE - destructive)
```bash
curl -X DELETE http://localhost:8080/api/v1/models/erase \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{}'
```

---

## ✅ Validaciones Implementadas

| Caso | Comportamiento |
|------|----------------|
| Crear modelo con ID duplicado | 400 Bad Request (ID ya existe) |
| Obtener modelo inexistente | 404 Not Found |
| **Eliminar modelo inexistente** | **404 Not Found** ← **NUEVO** |
| Request sin token | 401 Unauthorized |
| Token inválido | 401 Unauthorized |
| Login usuario no existe | 401 Unauthorized |
| Login contraseña incorrecta | 401 Unauthorized |

---

## 📊 Auditoría & Monitoreo

Todos los logs ahora muestran el método HTTP correcto:

```
[AUDIT] [ModelService] [USER:testuser] [GET /api/v1/models] [ACTION:LIST_MODELS] [RESULT:SUCCESS] [STATUS:200]
[AUDIT] [ModelService] [USER:testuser] [POST /api/v1/models] [ACTION:CREATE_MODEL] [RESULT:SUCCESS] [STATUS:201]
[AUDIT] [ModelService] [USER:testuser] [DELETE /api/v1/models] [ACTION:DELETE_MODEL] [RESULT:SUCCESS] [STATUS:200]
[AUDIT] [ModelService] [USER:testuser] [DELETE /api/v1/models] [ACTION:DELETE_MODEL] [RESULT:FAILED] [STATUS:404]
```

---

**Última actualización:** 5 de Marzo de 2026  
**Versión:** 3.0 (RESTful Compliant con métodos HTTP correctos)  
**Archivos:** `MELI_Challenge_RESTFUL.postman_collection.json`
