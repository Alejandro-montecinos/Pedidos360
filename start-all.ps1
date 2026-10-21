# =========================================================================
# Script para iniciar pedidosBackend, ventas y BFF en paralelo
# Ejecutar desde la raiz del proyecto: .\start-all.ps1
# =========================================================================

$root = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " Iniciando servicios del proyecto..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Matar procesos que ocupen los puertos 4545, 6767, 8080, 4200 y 9090 (limpieza previa)
@(4545, 6767, 8080, 4200, 9090) | ForEach-Object {
    $port = $_
    $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($conn) {
        Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
        Write-Host "   Puerto $port liberado." -ForegroundColor DarkGray
    }
}

Start-Sleep -Seconds 1

# Validar servicios base (MySQL y RabbitMQ)
$mysql = Get-NetTCPConnection -LocalPort 3306 -ErrorAction SilentlyContinue
if (-not $mysql) {
    Write-Host " [ADVERTENCIA] MySQL no parece estar corriendo en el puerto 3306." -ForegroundColor Red
} else {
    Write-Host " [OK] MySQL activo en puerto 3306." -ForegroundColor Green
}

$rabbit = Get-NetTCPConnection -LocalPort 5672 -ErrorAction SilentlyContinue
if (-not $rabbit) {
    Write-Host " [ADVERTENCIA] RabbitMQ no parece estar corriendo en el puerto 5672." -ForegroundColor Red
} else {
    Write-Host " [OK] RabbitMQ activo en puerto 5672." -ForegroundColor Green
}

# 1. Iniciar microservicio pedidosBackend (Consumidor RabbitMQ - Puerto 4545)
Write-Host ""
Write-Host "[1/5] Iniciando pedidosBackend (Puerto 4545)..." -ForegroundColor Yellow
$backendPath = Join-Path $root "pedidosBackend"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$backendPath'; Write-Host 'pedidosBackend (Consumidor RabbitMQ) - Puerto 4545' -ForegroundColor Green; .\mvnw.cmd spring-boot:run"

# Esperar 4 segundos
Write-Host "   Esperando 4s..."
Start-Sleep -Seconds 4

# 2. Iniciar microservicio ventas (Productor RabbitMQ - Puerto 6767)
Write-Host "[2/5] Iniciando ventas (Puerto 6767)..." -ForegroundColor Yellow
$ventasPath = Join-Path $root "ventas"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$ventasPath'; Write-Host 'ventas (Productor RabbitMQ) - Puerto 6767' -ForegroundColor Cyan; .\mvnw.cmd spring-boot:run"

# Esperar 4 segundos
Write-Host "   Esperando 4s..."
Start-Sleep -Seconds 4

# 3. Iniciar microservicio administrativo_ (API Admin RabbitMQ - Puerto 9090)
Write-Host "[3/5] Iniciando administrativo_ (API Admin RabbitMQ - Puerto 9090)..." -ForegroundColor Yellow
$adminPath = Join-Path $root "administrativo_"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$adminPath'; Write-Host 'administrativo (Admin RabbitMQ) - Puerto 9090' -ForegroundColor DarkYellow; .\mvnw.cmd spring-boot:run"

# Esperar 3 segundos
Write-Host "   Esperando 3s antes de iniciar el BFF..."
Start-Sleep -Seconds 3

# 4. Iniciar el BFF en una nueva ventana (Puerto 8080)
Write-Host "[4/5] Iniciando BFF Spring Boot (Puerto 8080)..." -ForegroundColor Yellow
$bffPath = Join-Path $root "bff\bff"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$bffPath'; Write-Host 'BFF - Puerto 8080' -ForegroundColor Magenta; .\mvnw.cmd spring-boot:run"

# Esperar 3 segundos
Write-Host "   Esperando 3s antes de iniciar Frontend..."
Start-Sleep -Seconds 3

# 5. Iniciar Frontend Angular en una nueva ventana (Puerto 4200)
Write-Host "[5/5] Iniciando Frontend Angular (Puerto 4200)..." -ForegroundColor Yellow
$frontendPath = Join-Path $root "pedidos360-frontend"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$frontendPath'; Write-Host 'Frontend Angular - Puerto 4200' -ForegroundColor Blue; npm start"

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " Todos los servicios iniciados:" -ForegroundColor Cyan
Write-Host "  - pedidosBackend : http://localhost:4545" -ForegroundColor Green
Write-Host "  - ventas         : http://localhost:6767" -ForegroundColor Cyan
Write-Host "  - administrativo : http://localhost:9090/admin.html" -ForegroundColor DarkYellow
Write-Host "  - BFF            : http://localhost:8080" -ForegroundColor Magenta
Write-Host "  - Frontend       : http://localhost:4200" -ForegroundColor Blue
Write-Host "  - RabbitMQ Panel : http://localhost:15672 (guest/guest)" -ForegroundColor Yellow
Write-Host "==========================================" -ForegroundColor Cyan
