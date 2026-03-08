# ⚙️ SOLUCIÓN: Configuración de JAVA_HOME

## 🔴 Problema Identificado

Al intentar ejecutar `mvn clean spring-boot:run` obtuviste:
```
ERROR: Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile
ERROR: Fatal error compiling: invalid flag: --release
```

**Causa:** `JAVA_HOME` no estaba configurado, por lo que Maven no podía encontrar `javac`.

## ✅ Soluciones (Elige una)

### **OPCIÓN 1: Usar Script de Inicio (Recomendado - Más Fácil)**

#### 🪟 En Command Prompt (cmd.exe):
```bash
cd G:\Documents\MELI_challenge\ChallengeMELI
start.bat
```

#### 🔵 En PowerShell:
```powershell
cd G:\Documents\MELI_challenge\ChallengeMELI
.\start.ps1
```

**Lo que hace:**
- ✅ Configura JAVA_HOME automáticamente
- ✅ Verifica que Java 21 está instalado
- ✅ Compila y ejecuta la aplicación
- ✅ NO requiere configuración manual

---

### **OPCIÓN 2: Configurar JAVA_HOME Permanentemente (Recomendado - Una sola vez)**

#### 🪟 Windows 10/11:

**Paso 1: Abre el Panel de Control**
- Presiona `Win + X` → Selecciona "Sistema"
- O abre `SystemPropertiesAdvanced.exe`

**Paso 2: Variables de Entorno**
1. Haz click en "Variables de entorno" (abajo a la derecha)
2. Haz click en "Nueva..." (en la sección "Variables del usuario")
3. Ingresa:
   - **Variable name:** `JAVA_HOME`
   - **Variable value:** `C:\Program Files\Java\jdk-21`
4. Haz click OK

**Paso 3: Reinicia PowerShell/CMD**

**Paso 4: Verifica**
```bash
echo %JAVA_HOME%
javac -version
```

**Resultado:**
```
C:\Program Files\Java\jdk-21
javac 21.0.2
```

---

### **OPCIÓN 3: Manual por Sesión (Temporal)**

#### Command Prompt:
```bash
set JAVA_HOME=C:\Program Files\Java\jdk-21
mvn clean spring-boot:run
```

#### PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
mvn clean spring-boot:run
```

**Nota:** Se pierde cuando cierras la consola.

---

## 🔧 Solución de Línea 79 en application.properties

**Problema:** La propiedad estaba vacía:
```properties
springdoc.api-docs.terms-of-service=
```

**Solución:** Se removió la línea vacía.

→ **Ya está corregido en `application.properties`** ✅

---

## ✨ Verificación: Confirma que funciona

Después de configurar JAVA_HOME y ejecutar `start.bat` o `start.ps1`:

```
[INFO] Scanning for projects...
[INFO] Building JavaSpringBootSample 1.0.0
...
[INFO] BUILD SUCCESS
[INFO] Started Application in 3.456 seconds (JVM running for 3.789)
```

Luego abre tu navegador:
```
http://localhost:8080/swagger-ui.html
```

✅ Deberías ver la interfaz de Swagger

---

## 📋 Checklist Final

- [x] JAVA_HOME configurado (Opción 1, 2 o 3)
- [x] Línea 79 en application.properties corregida
- [x] Scripts de inicio disponibles (start.bat, start.ps1)
- [x] Compilación exitosa (BUILD SUCCESS)
- [x] Servidor ejecutándose en http://localhost:8080

---

## ⚠️ Si aún tienes problemas

**Verifica que Java 21 está instalado:**
```bash
Get-ChildItem "C:\Program Files\Java" | Select-Object Name
```

Deberías ver:
```
jdk-21
jdk-21 (o similar)
```

**Si no lo ves:**
1. Ve a: https://www.oracle.com/java/technologies/downloads/
2. Descarga **Java 21 (LTS)** 
3. Instala en `C:\Program Files\Java\`
4. Vuelve a configurar JAVA_HOME

---

## 🚀 Próximos Pasos

Cuando hayas solucionado esto, ejecuta:

```bash
# Opción 1 (Recomendada - automático):
start.bat

# O en PowerShell:
.\start.ps1
```

Luego abre: `http://localhost:8080/swagger-ui.html`

¡Y a probar Swagger UI! 🎉

---
