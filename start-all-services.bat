@echo off
setlocal enabledelayedexpansion

echo ================================
echo Insurance System - Starting All Services
echo ================================

set "JAVA_HOME=C:\Program Files\Java\jdk-17.0.3"
set "PROJECT_DIR=C:\Users\jianjun.gao\insurance-product-system"

:: Create logs directory
if not exist "%PROJECT_DIR%\logs" mkdir "%PROJECT_DIR%\logs"

echo.
echo [1/14] Starting Eureka...
start "Eureka-8761" cmd /k "cd /d %PROJECT_DIR%\eureka-server && java -jar target\eureka-server-1.0.0.jar"

ping -n 16 127.0.0.1 >nul

echo [2/14] Starting Config Server...
start "Config-8089" cmd /k "cd /d %PROJECT_DIR%\config-service && java -jar target\config-service-1.0.0.jar"

ping -n 11 127.0.0.1 >nul

echo [3/14] Starting Spring Boot Admin...
start "Admin-8090" cmd /k "cd /d %PROJECT_DIR%\admin-server && java -jar target\admin-server-1.0.0.jar"

ping -n 11 127.0.0.1 >nul

echo [4/14] Starting Auth Service...
if not exist "%PROJECT_DIR%\auth-service\target\auth-service-1.0.0.jar" (
    echo    Building auth-service...
    cd /d %PROJECT_DIR%\auth-service
    call mvn clean package -DskipTests
    cd /d %PROJECT_DIR%
)
start "Auth-8092" cmd /k "cd /d %PROJECT_DIR%\auth-service && java -jar target\auth-service-1.0.0.jar"

ping -n 11 127.0.0.1 >nul

echo [5/14] Starting Gateway...
start "Gateway-8888" cmd /k "cd /d %PROJECT_DIR%\gateway && java -jar target\gateway-1.0.0.jar"

ping -n 11 127.0.0.1 >nul

echo [6/14] Starting Product Service...
start "Product-8080" cmd /k "cd /d %PROJECT_DIR%\backend && java -jar target\product-service-1.0.0.jar"

echo [7/14] Starting Customer Service...
start "Customer-8082" cmd /k "cd /d %PROJECT_DIR%\customer-service && java -jar target\customer-service-1.0.0.jar"

echo [8/14] Starting Application Service...
start "Application-8083" cmd /k "cd /d %PROJECT_DIR%\application-service && java -jar target\application-service-1.0.0.jar"

echo [9/14] Starting Underwriting Service...
start "Underwriting-8084" cmd /k "cd /d %PROJECT_DIR%\underwriting-service && java -jar target\underwriting-service-1.0.0.jar"

echo [10/14] Starting Policy Service...
start "Policy-8085" cmd /k "cd /d %PROJECT_DIR%\policy-service && java -jar target\policy-service-1.0.0.jar"

echo [11/14] Starting Claims Service...
start "Claims-8086" cmd /k "cd /d %PROJECT_DIR%\claims-service && java -jar target\claims-service-1.0.0.jar"

echo [12/14] Starting Finance Service...
start "Finance-8087" cmd /k "cd /d %PROJECT_DIR%\finance-service && java -jar target\finance-service-1.0.0.jar"

echo [13/14] Starting Message Service...
start "Message-8088" cmd /k "cd /d %PROJECT_DIR%\message-service && java -jar target\message-service-1.0.0.jar"

echo [14/14] Starting Report Service...
start "Report-8091" cmd /k "cd /d %PROJECT_DIR%\report-service && java -jar target\report-service-1.0.0.jar"

echo.
echo ================================
echo All services starting. Wait 45 seconds for registration.
echo ================================
echo.
echo Access URLs:
echo   Frontend(Nginx): http://localhost
echo   Gateway:         http://localhost:8888
echo   Eureka:          http://localhost:8761
echo   Admin:           http://localhost:8090
echo.
echo Ports:
echo   Product:     8080  Customer:    8082  Application: 8083
echo   Underwriting:8084  Policy:      8085  Claims:       8086
echo   Finance:     8087  Message:     8088  Report:       8091
echo   Auth:        8092  Eureka:      8761  Gateway:      8888
echo.
echo Login: admin / 123456
echo ================================

pause
