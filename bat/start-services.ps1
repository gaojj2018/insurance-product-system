$PROJECT_DIR = "C:\Users\jianjun.gao\insurance-product-system"
$LOG_DIR = "$PROJECT_DIR\logs"

# Create logs directory
if (-not (Test-Path $LOG_DIR)) { New-Item -ItemType Directory -Path $LOG_DIR | Out-Null }

Write-Host "================================"
Write-Host "Insurance System - Starting All Services"
Write-Host "================================"

# Start each service in background with proper output redirection
$services = @(
    @{name="eureka-server"; port=8761; dir="eureka-server"},
    @{name="config-service"; port=8089; dir="config-service"; wait=15},
    @{name="admin-server"; port=8090; dir="admin-server"; wait=10},
    @{name="auth-service"; port=8092; dir="auth-service"; wait=10},
    @{name="gateway"; port=8888; dir="gateway"; wait=10},
    @{name="product-service"; port=8080; dir="product-service"},
    @{name="customer-service"; port=8082; dir="customer-service"},
    @{name="application-service"; port=8083; dir="application-service"},
    @{name="underwriting-service"; port=8084; dir="underwriting-service"},
    @{name="policy-service"; port=8085; dir="policy-service"},
    @{name="claims-service"; port=8086; dir="claims-service"},
    @{name="finance-service"; port=8087; dir="finance-service"},
    @{name="message-service"; port=8088; dir="message-service"},
    @{name="report-service"; port=8091; dir="report-service"}
)

$start = Get-Date
$serviceNum = 1

foreach ($svc in $services) {
    $serviceName = $svc.name
    $serviceDir = $svc.dir
    $port = $svc.port
    $wait = if ($svc.wait) { $svc.wait } else { 0 }

    Write-Host "[$serviceNum/14] Starting $serviceName on port $port..."

    $jarPath = "$PROJECT_DIR\$serviceDir\target\$serviceName-1.0.0.jar"

    if (-not (Test-Path $jarPath)) {
        Write-Host "  JAR not found: $jarPath"
        $serviceNum++
        if ($wait -gt 0) { Start-Sleep -Seconds $wait }
        continue
    }

    # Start in background with no window
    $proc = Start-Process -FilePath "java" -ArgumentList "-jar", $jarPath `
        -WorkingDirectory "$PROJECT_DIR\$serviceDir" `
        -RedirectStandardOutput "$LOG_DIR\$($svc.name).log" `
        -RedirectStandardError "$LOG_DIR\$($svc.name).err" `
        -WindowStyle Hidden `
        -PassThru

    Write-Host "  Started PID: $($proc.Id)"

    $serviceNum++

    if ($wait -gt 0) {
        Start-Sleep -Seconds $wait
    }
}

Write-Host ""
Write-Host "================================"
Write-Host "All services started!"
Write-Host "Wait 45 seconds for registration."
Write-Host "================================"
Write-Host ""
Write-Host "Access URLs:"
Write-Host "  Frontend (Nginx): http://localhost"
Write-Host "  Gateway:          http://localhost:8888"
Write-Host "  Eureka:           http://localhost:8761"
Write-Host "  Admin:            http://localhost:8090"
Write-Host ""
Write-Host "Login: admin / 123456"
Write-Host "================================"
