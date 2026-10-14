# =========================================================================
# Script para iniciar pedidosBackend, ventas y BFF en paralelo
# Ejecutar desde la raiz del proyecto: .\start-all.ps1
# =========================================================================

$root = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " Iniciando servicios del proyecto..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Matar procesos Java que ocupen los puertos 4545, 6767 y 8080 (limpieza previa)
@(4545, 6767, 8080) | ForEach-Object {
    $port = $_
    $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($conn) {
        Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
        Write-Host "   Puerto $port liberado." -ForegroundColor DarkGray
    }
}

Start-Sleep -Seconds 1

# 1. Iniciar microservicio pedidosBackend (Consumidor RabbitMQ - Puerto 4545)
Write-Host ""
Write-Host "[1/3] Iniciando pedidosBackend (Puerto 4545)..." -ForegroundColor Yellow
$backendPath = Join-Path $root "pedidosBackend"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$backendPath'; Write-Host 'pedidosBackend (Consumidor RabbitMQ) - Puerto 4545' -ForegroundColor Green; .\mvnw.cmd spring-boot:run"

# Esperar 4 segundos
Write-Host "   Esperando 4s..."
Start-Sleep -Seconds 4

# 2. Iniciar microservicio ventas (Productor RabbitMQ - Puerto 6767)
Write-Host "[2/3] Iniciando ventas (Puerto 6767)..." -ForegroundColor Yellow
$ventasPath = Join-Path $root "ventas"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$ventasPath'; Write-Host 'ventas (Productor RabbitMQ) - Puerto 6767' -ForegroundColor Cyan; .\mvnw.cmd spring-boot:run"

# Esperar 4 segundos
Write-Host "   Esperando 4s antes de iniciar el BFF..."
Start-Sleep -Seconds 4

# 3. Iniciar el BFF en una nueva ventana (Puerto 8080)
Write-Host "[3/3] Iniciando BFF Spring Boot (Puerto 8080)..." -ForegroundColor Yellow
$bffPath = Join-Path $root "bff\bff"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$bffPath'; Write-Host 'BFF - Puerto 8080' -ForegroundColor Magenta; .\mvnw.cmd spring-boot:run"

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " Servicios iniciados:" -ForegroundColor Cyan
Write-Host "  - pedidosBackend : http://localhost:4545" -ForegroundColor Green
Write-Host "  - ventas         : http://localhost:6767" -ForegroundColor Cyan
Write-Host "  - BFF            : http://localhost:8080" -ForegroundColor Magenta
Write-Host "  - RabbitMQ Panel : http://localhost:15672 (guest/guest)" -ForegroundColor Yellow
Write-Host "==========================================" -ForegroundColor Cyan
