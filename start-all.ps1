# =========================================================================
# Script para iniciar pedidosBackend y BFF en paralelo
# Ejecutar desde la raiz del proyecto: .\start-all.ps1
# =========================================================================

$root = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " Iniciando servicios del proyecto..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Matar procesos Java que ocupen los puertos 4545 y 8080 (limpieza previa)
@(4545, 8080) | ForEach-Object {
    $port = $_
    $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($conn) {
        Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
        Write-Host "   Puerto $port liberado." -ForegroundColor DarkGray
    }
}

Start-Sleep -Seconds 1

# Iniciar el microservicio de productos en una nueva ventana (Puerto 4545)
Write-Host ""
Write-Host "[1/2] Iniciando pedidosBackend (Puerto 4545)..." -ForegroundColor Yellow
$backendPath = Join-Path $root "pedidosBackend"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$backendPath'; Write-Host 'pedidosBackend - Puerto 4545' -ForegroundColor Green; .\mvnw.cmd spring-boot:run"

# Esperar 5 segundos para que el primer servicio empiece a arrancar
Write-Host "   Esperando 5s antes de iniciar el BFF..."
Start-Sleep -Seconds 5

# Iniciar el BFF en una nueva ventana (Puerto 8080)
Write-Host "[2/2] Iniciando BFF Spring Boot (Puerto 8080)..." -ForegroundColor Yellow
$bffPath = Join-Path $root "bff\bff"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$bffPath'; Write-Host 'BFF - Puerto 8080' -ForegroundColor Magenta; .\mvnw.cmd spring-boot:run"

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " Ambos servicios iniciados en paralelo:" -ForegroundColor Cyan
Write-Host "  - pedidosBackend : http://localhost:4545" -ForegroundColor Green
Write-Host "  - BFF            : http://localhost:8080" -ForegroundColor Magenta
Write-Host "==========================================" -ForegroundColor Cyan
