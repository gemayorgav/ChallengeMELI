# MercadoLibre Item Detail API

Backend API RESTful que provee todos los datos necesarios para soportar una página de detalle de ítem, inspirada en MercadoLibre. Implementada con Spring Boot 3.2.5, autenticación JWT, base de datos en memoria H2, y patrones de resiliencia.

---

## Tabla de Contenidos

- [Decisiones arquitectónicas](#decisiones-arquitectónicas)
- [Stack tecnológico](#stack-tecnológico)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Modelo de datos](#modelo-de-datos)
- [Endpoints de la API](#endpoints-de-la-api)
- [Seguridad y autenticación](#seguridad-y-autenticación)
- [Resiliencia](#resiliencia)
- [Auditoría](#auditoría)
- [Testing](#testing)
- [Instrucciones de uso](#instrucciones-de-uso)
- [Colecciones Postman](#colecciones-postman)

---

## Decisiones arquitectónicas

### Arquitectura en capas

El proyecto sigue una arquitectura clásica de capas bien definidas, sin acoplamiento entre ellas:

```
Controller  →  Service  →  Repository  →  H2 (in-memory)
     ↓
  Security (JWT Filter + Spring Security)
     ↓
  GlobalExceptionHandler (manejo centralizado de errores)
     ↓
  AuditService (trazabilidad de todas las operaciones)
```

### Decisiones clave

| Decisión | Alternativa considerada | Justificación |
|---|---|---|
| **H2 in-memory** | PostgreSQL / MySQL | El enunciado indica simular persistencia; H2 arranca sin configuración externa |
| **JWT stateless** | Session-based / OAuth2 | Sin estado en servidor, escala horizontalmente, ideal para microservicios |
| **Spring Security** | Filtro manual | Framework robusto, integración nativa con Spring Boot, evita reimplementar lógica de seguridad |
| **Resilience4j** | Hystrix (deprecated) | Circuit Breaker + Retry nativos para Spring Boot 3, mantenido activamente |
| **DTO separados para Create/Update** | Un único DTO | `ModelOperationRequest` valida todos los campos (POST). `UpdateModelRequest` los hace opcionales (PUT), evitando falsos errores de validación en actualizaciones parciales |
| **`@PathVariable` para GET/{id} y DELETE/{id}** | `@RequestBody` o `@RequestParam` | Convención REST estándar; GET con cuerpo no está soportado por todos los clientes HTTP |
| **`@RestControllerAdvice` centralizado** | try/catch en cada método | Un único `GlobalExceptionHandler` garantiza respuestas de error consistentes en toda la API |
| **Auditoría persistida** | Logs planos | Cada operación queda registrada en tabla `AUDIT_LOG` con usuario, endpoint, resultado y timestamp, consultable y trazable |

---

## Stack tecnológico

| Componente | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 21 LTS |
| Framework | Spring Boot | 3.2.5 |
| Seguridad | Spring Security + JWT (jjwt) | 3.2.5 / 0.12.3 |
| Persistencia | Spring Data JPA + H2 | 3.2.5 / 2.1.214 |
| Resiliencia | Resilience4j | 2.1.0 |
| Documentación | SpringDoc OpenAPI (Swagger UI) | 2.4.0 |
| Observabilidad | Spring Boot Actuator | 3.2.5 |
| Logging | Logback + Logstash Encoder | JSON estructurado |
| Reducción boilerplate | Lombok | 1.18.30 |
| Build | Maven | 3.x |
| Testing | JUnit 5 + Mockito + MockMvc | Incluido en spring-boot-starter-test |

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/hackerrank/sample/
│   │   ├── Application.java              # Punto de entrada Spring Boot
│   │   ├── audit/
│   │   │   └── AuditService.java         # Trazabilidad de operaciones (con Circuit Breaker)
│   │   ├── config/
│   │   │   └── DataInitializer.java      # Crea usuarios de prueba al arrancar
│   │   ├── controller/
│   │   │   ├── AuthController.java       # POST /api/v1/auth/login
│   │   │   └── ModelController.java      # CRUD completo de modelos/items
│   │   ├── dto/
│   │   │   ├── ModelOperationRequest.java # DTO de creación (todos los campos obligatorios)
│   │   │   └── UpdateModelRequest.java   # DTO de actualización parcial (solo id obligatorio)
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java        # Manejo centralizado de errores
│   │   │   ├── BadResourceRequestException.java   # 400 Bad Request
│   │   │   └── NoSuchResourceFoundException.java  # 404 Not Found
│   │   ├── model/
│   │   │   ├── Model.java      # Entidad principal (item de MercadoLibre)
│   │   │   ├── User.java       # Entidad de usuario para autenticación
│   │   │   └── AuditLog.java   # Entidad de auditoría
│   │   ├── repository/
│   │   │   ├── ModelRepository.java
│   │   │   ├── UserRepository.java
│   │   │   └── AuditLogRepository.java
│   │   ├── security/
│   │   │   ├── WebSecurityConfig.java        # Configuración Spring Security + CORS
│   │   │   ├── JwtAuthenticationFilter.java  # Filtro que valida JWT en cada request
│   │   │   ├── JwtTokenProvider.java         # Genera y valida tokens JWT (HS512)
│   │   │   ├── AuthService.java              # Lógica de autenticación
│   │   │   ├── AuthRequest.java              # DTO login request
│   │   │   └── AuthResponse.java             # DTO login response
│   │   └── service/
│   │       ├── ModelService.java             # Interface del servicio
│   │       └── ModelServiceImpl.java         # Implementación con JPA
│   └── resources/
│       ├── application.properties            # Configuración de la aplicación
│       └── logback-spring.xml                # Configuración de logging JSON
└── test/
    └── java/com/hackerrank/sample/
        ├── controller/
        │   ├── AuthControllerTest.java      # 4 tests de integración (login)
        │   └── ModelControllerTest.java     # 20 tests de integración (CRUD completo)
        └── service/
            └── ModelServiceImplTest.java    # 13 tests unitarios (lógica de negocio)
```

---

## Modelo de datos

La entidad `Model` representa un ítem de MercadoLibre con todos sus atributos habituales:

```
Model
├── id                   Long         (PK)
├── name                 String       nombre del producto
├── description          String       descripción detallada
├── category             String       categoría
│
├── price                BigDecimal   precio base
├── discountPercentage   BigDecimal   % de descuento
├── discountedPrice      BigDecimal   precio con descuento
├── installmentMonths    Integer      meses de cuotas
│
├── color / size / material / brand / model   características físicas
│
├── stock                Integer
├── isAvailable          Boolean
│
├── isNew                Boolean      nuevo vs. usado
├── condition            String       Nueva / Usado / Recondicionado
│
├── rating               BigDecimal   calificación (0-5)
├── reviewCount          Integer
│
├── vendorId             Long         (NOT NULL)
├── vendorName           String
├── vendorRating         BigDecimal
├── vendorReviewCount    Integer
│
├── paymentMethods       List<String> (tabla MODEL_PAYMENT_METHODS)
├── imageUrls            List<String> (tabla MODEL_IMAGES)
├── mainImageUrl         String
│
├── status               String       ACTIVO / INACTIVO / SUSPENDIDO
├── hasWarranty / warrantyInfo
├── isFreeShipping / shippingCost / estimatedShippingDays
├── sku
│
├── createdAt / updatedAt / publishedAt   timestamps automáticos
```

---

## Endpoints de la API

### Autenticación

| Método | URL | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | ❌ Pública | Obtiene token JWT |

**Request body:**
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**Response 200:**
```json
{
  "success": true,
  "token": "eyJhbGci...",
  "username": "testuser",
  "role": "USER",
  "tokenType": "Bearer",
  "expiresIn": "1 hora"
}
```

**Usuarios preconfigurados** (creados automáticamente al arrancar):

| Usuario | Contraseña | Rol |
|---|---|---|
| `testuser` | `password123` | USER |
| `admin` | `admin123` | ADMIN |

---

### Items (Modelos)

> Todos los endpoints de esta sección requieren el header:
> `Authorization: Bearer <token>`

| Método | URL | Descripción | Código éxito |
|---|---|---|---|
| `POST` | `/api/v1/models` | Crear nuevo ítem | `201 Created` |
| `GET` | `/api/v1/models` | Listar ítems (paginado) | `200 OK` |
| `GET` | `/api/v1/models/{id}` | **Detalle de ítem por ID** | `200 OK` |
| `PUT` | `/api/v1/models` | Actualizar ítem (parcial) | `200 OK` |
| `DELETE` | `/api/v1/models/{id}` | Eliminar ítem por ID | `200 OK` |
| `DELETE` | `/api/v1/models/erase` | Eliminar todos los ítems | `200 OK` |

#### GET `/api/v1/models/{id}` — Detalle de ítem (endpoint principal)

Retorna todos los datos necesarios para renderizar una página de detalle de producto.

**Response 200:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Laptop Lenovo IdeaPad",
    "description": "Excelente laptop para trabajo y estudio",
    "category": "Electrónica",
    "price": 1500.00,
    "discountPercentage": 10.00,
    "discountedPrice": 1350.00,
    "brand": "Lenovo",
    "model": "IdeaPad 3",
    "color": "Gris",
    "stock": 10,
    "isAvailable": true,
    "isNew": true,
    "condition": "Nueva",
    "rating": 4.50,
    "reviewCount": 128,
    "vendorId": 1,
    "vendorName": "Tienda Oficial Lenovo",
    "vendorRating": 4.80,
    "paymentMethods": ["Tarjeta de Crédito", "Efectivo", "Transferencia"],
    "imageUrls": ["https://img.example.com/laptop1.jpg"],
    "mainImageUrl": "https://img.example.com/laptop1.jpg",
    "isFreeShipping": true,
    "estimatedShippingDays": 3,
    "hasWarranty": true,
    "warrantyInfo": "12 meses garantía oficial",
    "status": "ACTIVO"
  },
  "timestamp": "2026-03-08T10:30:00"
}
```

**Response 404:**
```json
{
  "success": false,
  "message": "No model with given id found.",
  "timestamp": "2026-03-08T10:30:00"
}
```

#### GET `/api/v1/models` — Listado paginado

**Query params opcionales:**

| Parámetro | Default | Descripción |
|---|---|---|
| `page` | `0` | Número de página (base 0) |
| `size` | `10` | Elementos por página |
| `sort` | `id` | Campo de ordenamiento |
| `direction` | `asc` | `asc` o `desc` |

**Response 200:**
```json
{
  "success": true,
  "data": [...],
  "pagination": {
    "currentPage": 0,
    "pageSize": 10,
    "totalElements": 50,
    "totalPages": 5,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

#### POST `/api/v1/models` — Crear ítem

Campos **obligatorios** en el body:

| Campo | Tipo | Validación |
|---|---|---|
| `id` | Long | Positivo, único |
| `name` | String | 3–255 caracteres |
| `description` | String | 10–2000 caracteres |
| `category` | String | 2–100 caracteres |
| `price` | BigDecimal | > 0 |
| `brand` | String | 2–100 caracteres |
| `model` | String | 2–100 caracteres |
| `stock` | Integer | ≥ 0 |
| `isAvailable` | Boolean | — |
| `isNew` | Boolean | — |
| `condition` | String | `Nueva`, `Usado` o `Recondicionado` |
| `vendorId` | Long | Positivo |
| `vendorName` | String | 2–255 caracteres |
| `paymentMethods` | List\<String\> | Al menos 1 |
| `imageUrls` | List\<String\> | Al menos 1 |
| `mainImageUrl` | String | No vacío |

#### PUT `/api/v1/models` — Actualizar ítem (parcial)

Solo el campo `id` es obligatorio. El resto son opcionales — se actualiza únicamente lo que se envíe.

```json
{
  "id": 1,
  "price": 1299.99,
  "stock": 5
}
```

#### DELETE `/api/v1/models/{id}` — Eliminar ítem

```
DELETE /api/v1/models/1
```

---

### Observabilidad

| Método | URL | Auth | Descripción |
|---|---|---|---|
| `GET` | `/api/v1/actuator/health` | ❌ Pública | Estado de la aplicación |
| `GET` | `/api/v1/actuator/info` | ❌ Pública | Información de la app |
| `GET` | `/api/v1/actuator/metrics` | ❌ Pública | Métricas de la JVM |

---

### Documentación interactiva (Swagger UI)

Disponible sin autenticación en: `http://localhost:8080/swagger-ui/index.html`

La especificación OpenAPI 3.0 está en: `http://localhost:8080/v3/api-docs`

---

## Seguridad y autenticación

```
Request
   │
   ▼
JwtAuthenticationFilter
   ├── Rutas públicas (/api/v1/auth/**, /swagger-ui/**, /actuator/health) → bypass
   ├── Sin header Authorization → 401 Unauthorized
   ├── Token inválido/expirado  → 401 Unauthorized
   └── Token válido → setea SecurityContext → continúa la cadena
   │
   ▼
Spring Security (WebSecurityConfig)
   └── .anyRequest().authenticated()
```

- Algoritmo de firma: **HS512**
- Expiración del token: **1 hora** (configurable en `application.properties`)
- La sesión es **stateless** — no se almacena en servidor
- CSRF deshabilitado (API REST sin formularios)
- CORS configurado para aceptar cualquier origen (ajustable para producción)

---

## Resiliencia

Todos los endpoints del `ModelController` y el `AuditService` están protegidos con:

### Circuit Breaker (Resilience4j)

| Configuración | ModelService | AuditService |
|---|---|---|
| Ventana deslizante | 10 llamadas | 5 llamadas |
| Umbral de fallo | 50% | 50% |
| Tiempo en estado abierto | 10 segundos | 5 segundos |
| Llamadas en half-open | 3 | — |

Cuando el circuit breaker está abierto, cada endpoint tiene su propio método **fallback** que retorna `503 Service Unavailable` con un mensaje descriptivo, en lugar de propagar el error.

### Retry

| Configuración | ModelService | AuditService |
|---|---|---|
| Intentos máximos | 3 | 2 |
| Tiempo entre intentos | 1 segundo | 500ms |

---

## Auditoría

Cada operación queda registrada automáticamente en la tabla `AUDIT_LOG` con la siguiente estructura:

```
AuditLog
├── id          Long        (PK, autoincremental)
├── username    String      usuario que ejecutó la operación
├── service     String      servicio involucrado
├── method      String      método HTTP (GET, POST, PUT, DELETE)
├── endpoint    String      ruta de la llamada
├── action      String      acción lógica (CREATE_MODEL, GET_MODEL, etc.)
├── result      String      SUCCESS / FAILED
├── statusCode  String      código HTTP resultado
├── details     String      información adicional
└── timestamp   LocalDateTime
```

El registro de auditoría también usa Circuit Breaker y Retry, garantizando que un fallo en la auditoría no interrumpa la operación principal.

---

## Testing

El proyecto cuenta con **37 tests** distribuidos en tres clases:

```
Tests run: 37, Failures: 0, Errors: 0, Skipped: 0
```

### `AuthControllerTest` — 4 tests de integración

| Test | Escenario | Resultado esperado |
|---|---|---|
| `login_ValidCredentials_Returns200WithToken` | Credenciales correctas (testuser) | 200 + token JWT |
| `login_AdminCredentials_Returns200WithToken` | Credenciales correctas (admin) | 200 + token JWT |
| `login_InvalidPassword_Returns401` | Contraseña incorrecta | 401 Unauthorized |
| `login_UnknownUser_Returns401` | Usuario inexistente | 401 Unauthorized |

### `ModelControllerTest` — 20 tests de integración

Usa contexto Spring completo (`@SpringBootTest`), base H2 limpia antes de cada test (`@BeforeEach`), y token JWT generado programáticamente.

| Endpoint | Tests |
|---|---|
| `POST /models` | Creación exitosa (201), ID duplicado (400), validación (400), sin token (401), token inválido (401) |
| `GET /models` | Lista paginada (200), paginación custom, tabla vacía |
| `GET /models/{id}` | Item existente (200), ID inexistente (404) |
| `PUT /models` | Actualización parcial nombre (200), múltiples campos (200), ID inexistente (404), sin ID (400), precio negativo (400) |
| `DELETE /models/{id}` | Eliminación exitosa (200), ID inexistente (404), sin auth (401) |
| `DELETE /models/erase` | Eliminar todos (200), tabla vacía (200) |

### `ModelServiceImplTest` — 13 tests unitarios

Aísla la capa de servicio con Mockito, sin levantar Spring context. Verifica la lógica de negocio pura:

| Método | Tests |
|---|---|
| `createModel` | Persiste correctamente, lanza `BadResourceRequestException` si ID duplicado |
| `getModelById` | Retorna modelo, lanza `NoSuchResourceFoundException` si no existe |
| `updateModel` | Actualiza correctamente, lanza excepción si no existe |
| `deleteModelById` | Elimina correctamente, lanza excepción si no existe |
| `deleteAllModels` | Invoca `deleteAllInBatch` |
| `getAllModels` | Lista completa y lista vacía |
| `getAllModelsPaginated` | Primera página y segunda página |

### Ejecutar los tests

```bash
# Todos los tests
mvn test

# Solo los tests nuevos
mvn test -Dtest="ModelServiceImplTest,AuthControllerTest,ModelControllerTest"
```

---

## Instrucciones de uso

### Prerrequisitos

- **Java 21** (JDK 21 LTS)
- **Maven 3.6+**

### Clonar y ejecutar

```bash
# 1. Clonar el repositorio
git clone https://github.com/gemayorgav/ChallengeMELI.git
cd ChallengeMELI

# 2. Compilar
mvn clean package -DskipTests

# 3. Ejecutar
java -jar target/sample-1.0.0.jar
```

O bien con Maven directamente:

```bash
mvn clean spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`.

### Variables de entorno configurables

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `jwt.secret` | `MyVerySecure...` | Secreto para firmar tokens JWT |
| `jwt.expiration` | `3600000` | Expiración del token en milisegundos (1 hora) |
| `server.port` | `8080` | Puerto de la aplicación |

---

## Colecciones Postman

El repositorio incluye una colección Postman definitiva:

| Archivo | Descripción |
|---|---|
| `MELI_Challenge_API.postman_collection.json` | Colección completa con todos los endpoints, casos de error y flujo guiado |

### Setup en Postman

1. Importar `MELI_Challenge_API.postman_collection.json`
2. Verificar que la variable `base_url` apunte a `http://localhost:8080`
3. Ejecutar **"[LOGIN] testuser"** — el token JWT se guarda automáticamente en `{{token}}`
4. Explorar las carpetas en orden o ejecutar el **"7. FLUJO COMPLETO"** como demostración

### Estructura de la colección

| Carpeta | Contenido |
|---|---|
| **0. SETUP** | Login con testuser/admin, caso de credenciales inválidas |
| **1. OBSERVABILIDAD** | Health check, métricas JVM, Swagger UI, OpenAPI JSON |
| **2. CREAR ÍTEMS** | POST completo, ID duplicado, validación incompleta, sin token |
| **3. LISTAR ÍTEMS** | Paginación default/custom, orden por precio/nombre, segunda página |
| **4. DETALLE DE ÍTEM** | GET por id existente ×3, id inexistente → 404 |
| **5. ACTUALIZAR ÍTEM** | PUT parcial ×4 variantes, id inexistente → 404, precio negativo → 400 |
| **6. ELIMINAR ÍTEMS** | DELETE por id, id ya eliminado, sin token → 401, erase all |
| **7. FLUJO COMPLETO** | 8 pasos secuenciales con assertions automáticas |

---

## Manejo de errores

Todas las respuestas de error siguen un formato consistente:

```json
{
  "timestamp": "2026-03-08T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "fieldErrors": {
    "price": "El precio debe ser mayor a 0"
  }
}
```

| Código | Causa |
|---|---|
| `400` | Validación fallida (campos inválidos o ID duplicado) |
| `401` | Token ausente, inválido o expirado |
| `404` | Recurso no encontrado |
| `405` | Método HTTP no soportado en la ruta |
| `503` | Circuit Breaker abierto (servicio temporalmente no disponible) |
| `500` | Error interno inesperado |

---

## Diagrama de secuencia — Detalle de ítem

```
Cliente          API Gateway       Spring Security     ModelController    ModelService      H2 DB
   │                  │                  │                   │                 │              │
   │─── GET /api/v1/models/1 ──────────►│                   │                 │              │
   │                  │──── JWT válido? ►│                   │                 │              │
   │                  │                  │── setAuth() ─────►│                 │              │
   │                  │                  │                   │─ getModelById(1)►│              │
   │                  │                  │                   │                 │─ findById(1)─►│
   │                  │                  │                   │                 │◄── Model ────│
   │                  │                  │                   │◄── Model ───────│              │
   │◄── 200 {data: Model, success: true} ────────────────────│                 │              │
```
