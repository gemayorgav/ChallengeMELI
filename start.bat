@echo off
REM ============================================
REM Script para iniciar la aplicación MELI
REM ============================================
REM Configura JAVA_HOME automáticamente a Java 21
REM Inicia el servidor Spring Boot con compilación

echo.
echo ========================================
echo     MELI Challenge API - Phase 2
echo ========================================
echo.

REM Configurar JAVA_HOME a Java 21 (reemplazo obligatorio)
echo [INFO] Configurando JAVA_HOME a Java 21...
set JAVA_HOME=C:\Program Files\Java\jdk-21
echo [INFO] JAVA_HOME = %JAVA_HOME%

REM Verificar que JAVA_HOME existe
if not exist "%JAVA_HOME%\bin\javac.exe" (
    echo [ERROR] No se encontró Java 21 en %JAVA_HOME%
    echo [ERROR] Por favor, instala Java 21 en C:\Program Files\Java\jdk-21
    pause
    exit /b 1
)

echo [INFO] Java version:
"%JAVA_HOME%\bin\java" -version

echo.
echo [INFO] Puerto: http://localhost:8080
echo [INFO] Swagger: http://localhost:8080/swagger-ui.html
echo [INFO] Health: http://localhost:8080/api/v1/actuator/health
echo.
echo [INFO] Compilando y ejecutando la aplicación...
echo.

REM Ejecutar Maven
mvn clean spring-boot:run

pause
