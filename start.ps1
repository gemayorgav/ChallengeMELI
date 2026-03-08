# ============================================
# Script PowerShell para iniciar la aplicación
# ============================================
# Configura JAVA_HOME automáticamente
# Inicia el servidor Spring Boot

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "     MELI Challenge API - Startup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar JAVA_HOME si no está definido
if ([string]::IsNullOrEmpty($env:JAVA_HOME)) {
    Write-Host "[INFO] Configurando JAVA_HOME..." -ForegroundColor Yellow
    $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
    Write-Host "[INFO] JAVA_HOME = $env:JAVA_HOME" -ForegroundColor Green
} else {
    Write-Host "[INFO] JAVA_HOME ya configurado = $env:JAVA_HOME" -ForegroundColor Green
}

# Verificar que JAVA_HOME existe
if (-not (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
    Write-Host "[ERROR] No se encontró JAVA en $env:JAVA_HOME" -ForegroundColor Red
    Write-Host "[ERROR] Por favor, instala Java 21" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}

Write-Host "[INFO] Java version:" -ForegroundColor Yellow
& "$env:JAVA_HOME\bin\java" -version

Write-Host ""
Write-Host "[INFO] Compilando y ejecutando la aplicación..." -ForegroundColor Yellow
Write-Host ""

# Ejecutar Maven
cd (Split-Path -Parent $MyInvocation.MyCommand.Path)
mvn clean spring-boot:run

Read-Host "Presiona Enter para salir"
