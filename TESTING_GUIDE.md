# 🧪 Guía de Pruebas - MELI Challenge API

## Cómo Probar los Endpoints

### 1️⃣ **Iniciar el Servidor**

En una terminal, navega a la carpeta del proyecto y ejecuta:

```bash
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
cd "G:\Documents\MELI_challenge\ChallengeMELI"
mvn spring-boot:run
```

Espera hasta ver:
```
Started Application in X.XXX seconds
```

El servidor estará disponible en: **http://localhost:8080**

---

### 2️⃣ **Importar Colección en Postman**

1. Abre **Postman**
2. Click en **File** → **Import**
3. Selecciona el archivo: **MELI_Challenge.postman_collection.json**
4. Click en **Import**

---

### 3️⃣ **Ejecutar las Pruebas**

La colección incluye 12 requests ordenados:

| # | Request | Método | Endpoint | Descripción |
|---|---------|--------|----------|-------------|
| 1 | Limpiar DB | DELETE | `/erase` | Limpia toda la base de datos |
| 2 | Obtener todos (vacío) | GET | `/model` | Confirma que está vacía |
| 3 | Crear iPhone | POST | `/model` | Crea modelo con ID 1 |
| 4 | Crear Samsung | POST | `/model` | Crea modelo con ID 2 |
| 5 | Crear Pixel | POST | `/model` | Crea modelo con ID 3 |
| 6 | Obtener por ID | GET | `/model/1` | Obtiene el iPhone |
| 7 | ID inexistente | GET | `/model/999` | Prueba error 404 |
| 8 | Obtener todos | GET | `/model` | Obtiene los 3 modelos |
| 9 | Duplicado (error) | POST | `/model` | Prueba error 400 |
| 10 | Eliminar | DELETE | `/model/2` | Elimina Samsung |
| 11 | Verificar borrado | GET | `/model/2` | Confirma que no existe |
| 12 | Obtener restantes | GET | `/model` | Confirma 2 modelos |

---

## 📊 Respuestas Esperadas

### ✅ Crear Modelo (201)
```json
POST /model
{
  "id": 1,
  "name": "iPhone 15 Pro Max"
}

Response: 201 Created
```

### ✅ Obtener Todos (200)
```json
GET /model

Response: 200 OK
[
  {
    "id": 1,
    "name": "iPhone 15 Pro Max"
  },
  {
    "id": 3,
    "name": "Google Pixel 8 Pro"
  }
]
```

### ✅ Obtener por ID (200)
```json
GET /model/1

Response: 200 OK
{
  "id": 1,
  "name": "iPhone 15 Pro Max"
}
```

### ❌ ID Duplicado (400)
```json
POST /model
{
  "id": 1,
  "name": "Algo"
}

Response: 400 Bad Request
Message: "Model with same id exists."
```

### ❌ ID No Encontrado (404)
```json
GET /model/999

Response: 404 Not Found
Message: "No model with given id found."
```

---

## 🔄 Flujo de Datos (H2 + JPA/Hibernate)

```
REQUEST POSTMAN
    ↓
Spring Boot Controller
    ↓
Service (Lógica de negocio)
    ↓
JPA Repository Interface
    ↓
Hibernate (Traduce a SQL)
    ↓
H2 Database (Almacena en memoria)
    ↓
RESPONSE
```

### Ejemplo Real:
```
POST /model { "id": 1, "name": "iPhone" }
    ↓
ModelController.createNewModel()
    ↓
ModelService.createModel()
    ↓
ModelRepository.save(model)
    ↓
Hibernate genera:
INSERT INTO MODEL (ID, NAME) VALUES (1, 'iPhone')
    ↓
H2 almacena en memoria
    ↓
201 Created
```

---

## 🛠️ Comandos Útiles

**Ejecutar tests:**
```bash
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
mvn clean test
```

**Compilar:**
```bash
mvn clean install
```

**Ver logs en tiempo real:**
```bash
mvn spring-boot:run (ya incluye logs)
```

---

## 📌 Notas Importantes

- **H2 en memoria**: Los datos se pierden al cerrar el servidor
- **No necesita BD externa**: Todo funciona en RAM
- **DDL automático**: Las tablas se crean/borran automáticamente
- **Validación de duplicados**: El servicio revisa antes de insertar

¡Listo para probar! 🚀
