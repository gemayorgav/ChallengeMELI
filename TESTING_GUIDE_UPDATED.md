# MELI Challenge - Guía de Testing Actualizada

## 🔐 Autenticación JWT

La API ahora requiere **autenticación mediante JWT tokens** en todos los endpoints protegidos.

### Obtener un Token

**Endpoint:** `POST http://localhost:8080/auth/login`

**Usuarios de Prueba:**
- Usuario: `testuser` | Contraseña: `password123` | Rol: `USER`
- Usuario: `admin` | Contraseña: `admin123` | Rol: `ADMIN`

**Ejemplo de Request:**
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**Respuesta exitosa (200):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTcwMTY1MzAwMCwiZXhwIjoxNzAxNjU2NjAwfQ...",
  "username": "testuser",
  "role": "USER",
  "timestamp": "2026-03-04T22:58:45.123",
  "expiresIn": "1 hora",
  "tokenType": "Bearer"
}
```

### Usar el Token

Para todos los endpoints protegidos, incluye el token en el header `Authorization`:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTcwMTY1MzAwMCwiZXhwIjoxNzAxNjU2NjAwfQ...
```

---

## 📋 Estructura de Endpoints Actualizada

### ✅ Cambios Principales

| Operación | Endpoint Anterior | Endpoint Nuevo | Método | Cambio |
|-----------|------------------|-----------------|--------|--------|
| Listar | GET `/model` | POST `/api/v1/models/list` | POST | Método POST, JSON body, ruta versioned |
| Crear | POST `/model` | POST `/api/v1/models/create` | POST | JSON body, no URL params |
| Obtener | GET `/model/{id}` | POST `/api/v1/models/details` | POST | ID en JSON body, no en URL |
| Eliminar | DELETE `/model/{id}` | POST `/api/v1/models/delete` | POST | ID en JSON body, método POST |
| Limpiar | DELETE `/erase` | POST `/api/v1/models/erase` | POST | Método POST, ruta versioned |

**Principio de Diseño:** Todos los parámetros van en el JSON body, no en la URL (REST best practices).

---

## 🚀 Flujo de Testing Completo

### Paso 1: Autenticación
1. Ejecuta: **"1. Login - testuser"** en la sección AUTENTICACIÓN
2. **Copia el valor del campo `token` de la respuesta**
3. En Postman, ve a la pestaña **Variables** en la colección
4. **Pega el token en la variable `{{token}}`** (quitar el prefijo "Bearer ")

### Paso 2: Limpiar Base de Datos
1. Ejecuta: **"1. Limpiar DB - Erase"** en CRUD OPERATIONS
2. Respuesta esperada:
```json
{
  "success": true,
  "message": "Base de datos limpiada",
  "timestamp": "2026-03-04T23:00:00.000"
}
```

### Paso 3: Verificar que está vacía
1. Ejecuta: **"2. Listar modelos"**
2. Respuesta esperada: `{"success": true, "data": []}`

### Paso 4: Crear Modelos
1. Ejecuta: **"3. Crear modelo - iPhone 15"** 
2. Respuesta esperada (201 Created):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "iPhone 15"
  },
  "timestamp": "2026-03-04T23:00:15.123"
}
```

3. Ejecuta: **"4. Crear modelo - iPhone 16"**
4. Ejecuta: **"5. Crear modelo - Samsung Galaxy S24"**

### Paso 5: Listar Modelos
1. Ejecuta: **"6. Listar modelos creados"**
2. Respuesta esperada: Array con 3 modelos

### Paso 6: Obtener Detalles
1. Ejecuta: **"7. Obtener detalle - ID 1"**
2. Respuesta esperada: Modelo completo de iPhone 15

### Paso 7: Eliminar Modelo
1. Ejecuta: **"9. Eliminar modelo - ID 3"**
2. Respuesta esperada (200 OK):
```json
{
  "success": true,
  "message": "Modelo eliminado exitosamente",
  "timestamp": "2026-03-04T23:00:45.123"
}
```

3. Ejecuta: **"10. Listar modelos restantes"**
4. Debe mostrar solo 2 modelos (iPhone 15 e iPhone 16)

---

## 🔒 Pruebas de Seguridad

### Sin Token (Esperado: 401 Unauthorized)
- Ejecuta: **"1. Sin token - Debe fallar (401)"**
- Respuesta: `401 Unauthorized` con mensaje de error

### Token Inválido (Esperado: 401 Unauthorized)
- Ejecuta: **"2. Token inválido - Debe fallar (401)"**
- Respuesta: `401 Unauthorized` con mensaje de error

### Login Fallido - Usuario no existe
- Ejecuta: **"3. Login fallido - Usuario no existe"**
- Respuesta: `401 Unauthorized` - "Usuario no encontrado"

### Login Fallido - Contraseña incorrecta
- Ejecuta: **"4. Login fallido - Contraseña incorrecta"**
- Respuesta: `401 Unauthorized` - "Contraseña incorrecta"

---

## 📊 Monitoreo y Auditoría

### Logs en Consola

Cada operación genera un log en formato:
```
[AUDIT] [SERVICE] [USER:username] [METHOD ENDPOINT] [ACTION:action] [RESULT:result] [STATUS:code] [TIME:timestamp]
```

**Ejemplo:**
```
[AUDIT] [ModelService] [USER:testuser] [POST /api/v1/models/create] [ACTION:CREATE] [RESULT:SUCCESS] [STATUS:201] [TIME:2026-03-04T23:00:15.123]
```

### Base de Datos de Auditoría

Todas las operaciones se registran en la tabla `AUDIT_LOG`:
- Usuario que ejecutó la acción
- Servicio llamado
- Método HTTP y endpoint
- Resultado (SUCCESS/FAILED)
- Código de estado HTTP
- Marca de tiempo

---

## 🛡️ Características de Seguridad Implementadas

### ✅ Autenticación JWT
- Tokens con expiración de 1 hora
- Algoritmo HS512 con clave secreta
- Validación en cada request

### ✅ Autorización
- JwtAuthenticationFilter valida tokens
- Solo endpoints en `/api/v1/` requieren autenticación
- Endpoints `/auth/*` y `/` son públicos

### ✅ Auditoría
- Registro de todas las operaciones con usuario
- Persistencia en H2 database
- Logs en consola para monitoreo

### ✅ Resiliencia
- **Circuit Breaker:** Se abre si 50% de requests fallan en 10 sliding calls
- **Retry:** Reintenta hasta 3 veces con backoff de 1 segundo
- **Fallback:** Retorna 503 SERVICE_UNAVAILABLE si circuit está abierto

---

## 🧪 Ejemplos de Requests con curl

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### Listar Modelos
```bash
curl -X POST http://localhost:8080/api/v1/models/list \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{}'
```

### Crear Modelo
```bash
curl -X POST http://localhost:8080/api/v1/models/create \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"iPhone 15"}'
```

### Obtener Detalles
```bash
curl -X POST http://localhost:8080/api/v1/models/details \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{"id":1}'
```

### Eliminar Modelo
```bash
curl -X POST http://localhost:8080/api/v1/models/delete \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{"id":1}'
```

### Erase All
```bash
curl -X POST http://localhost:8080/api/v1/models/erase \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{}'
```

---

## 📝 Notas Importantes

1. **Token Expiration:** Los tokens expiran después de 1 hora. Necesitarás hacer un nuevo login.
2. **Variable {{token}}:** Actualiza la variable cada vez que hagas login
3. **Content-Type:** Siempre usa `application/json` en headers
4. **Bearer Prefix:** El header debe ser: `Authorization: Bearer <token>` (con espacio)
5. **JSON Body:** Incluso en requests sin parámetros, envía un JSON vacío `{}`

---

## 🔄 Stack Tecnológico

- **Java 21** - LTS version
- **Spring Boot 3.2.5** - Latest Spring Boot
- **H2 Database** - Base de datos en memoria
- **JPA/Hibernate** - ORM
- **JJWT 0.12.3** - JWT token generation/validation
- **Resilience4j 2.1.0** - Circuit Breaker & Retry
- **Lombok 1.18.30** - Reduce boilerplate
- **Maven 3.9.6** - Build tool

---

**Última actualización:** 4 de Marzo de 2026
**Versión:** 2.0 (Con autenticación JWT y endpoints versioned)
