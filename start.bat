@echo off
REM ============================================
REM Script para iniciar la aplicación MELI
REM ============================================
REM Configura JAVA_HOME automáticamente
REM Inicia el servidor Spring Boot

echo.
echo ========================================
echo     MELI Challenge API - Startup
echo ========================================
echo.

REM Configurar JAVA_HOME si no está definido
if "%JAVA_HOME%"=="" (
    echo [INFO] Configurando JAVA_HOME...
    set JAVA_HOME=C:\Program Files\Java\jdk-21
    echo [INFO] JAVA_HOME = %JAVA_HOME%
) else (
    echo [INFO] JAVA_HOME ya configurado = %JAVA_HOME%
)

REM Verificar que JAVA_HOME existe
if not exist "%JAVA_HOME%\bin\javac.exe" (
    echo [ERROR] No se encontró JAVA en %JAVA_HOME%
    echo [ERROR] Por favor, instala Java 21
    pause
    exit /b 1
)

echo [INFO] Java version:
%JAVA_HOME%\bin\java -version

echo.
echo [INFO] Compilando y ejecutando la aplicación...
echo.

REM Ejecutar Maven
mvn clean spring-boot:run

pause
