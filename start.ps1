# ============================================
# Script PowerShell para iniciar la aplicación
# ============================================
# Configura JAVA_HOME automáticamente a Java 21
# Inicia el servidor Spring Boot con compilación

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "     MELI Challenge API - Phase 2" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar JAVA_HOME a Java 21 (reemplazo obligatorio)
Write-Host "[INFO] Configurando JAVA_HOME a Java 21..." -ForegroundColor Yellow
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
Write-Host "[INFO] JAVA_HOME = $env:JAVA_HOME" -ForegroundColor Green

# Verificar que JAVA_HOME existe
if (-not (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
    Write-Host "[ERROR] No se encontró Java 21 en $env:JAVA_HOME" -ForegroundColor Red
    Write-Host "[ERROR] Por favor, instala Java 21 en C:\Program Files\Java\jdk-21" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}

Write-Host "[INFO] Java version:" -ForegroundColor Yellow
& "$env:JAVA_HOME\bin\java" -version

# Verificar y limpiar puerto 8080
Write-Host ""
Write-Host "[INFO] Verificando puerto 8080..." -ForegroundColor Yellow
try {
    $netstatOutput = netstat -ano | Select-String ":8080"
    if ($netstatOutput) {
        $parts = $netstatOutput -split '\s+' | Where-Object { $_ }
        $pid = $parts[-1]
        
        if ($pid -and $pid -ne "PID") {
            Write-Host "[WARN] Proceso existente en puerto 8080 (PID: $pid), terminando..." -ForegroundColor Yellow
            Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
            Start-Sleep -Seconds 2
            Write-Host "[INFO] Puerto 8080 liberado" -ForegroundColor Green
        }
    }
} catch {
    Write-Host "[INFO] Puerto 8080 disponible" -ForegroundColor Green
}

Write-Host ""
Write-Host "[INFO] Compilando y ejecutando la aplicación..." -ForegroundColor Yellow
Write-Host "[INFO] URL: http://localhost:8080" -ForegroundColor Cyan
Write-Host "[INFO] Swagger: http://localhost:8080/swagger-ui.html" -ForegroundColor Cyan
Write-Host "[INFO] Health: http://localhost:8080/api/v1/actuator/health" -ForegroundColor Cyan
Write-Host "" -ForegroundColor Blue

# Ejecutar Maven
cd (Split-Path -Parent $MyInvocation.MyCommand.Path)
mvn clean spring-boot:run

Read-Host "Presiona Enter para salir"
