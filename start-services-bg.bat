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
echo Starting services in background...
echo.

:: Start Eureka
start /b "" cmd /c "cd /d %PROJECT_DIR%\eureka-server && java -jar target\eureka-server-1.0.0.jar >%PROJECT_DIR%\logs\eureka.log 2>&1"
echo [1/14] Eureka started

ping -n 16 127.0.0.1 >nul

:: Start Config Server
start /b "" cmd /c "cd /d %PROJECT_DIR%\config-service && java -jar target\config-service-1.0.0.jar >%PROJECT_DIR%\logs\config.log 2>&1"
echo [2/14] Config Server started

ping -n 11 127.0.0.1 >nul

:: Start Admin Server
start /b "" cmd /c "cd /d %PROJECT_DIR%\admin-server && java -jar target\admin-server-1.0.0.jar >%PROJECT_DIR%\logs\admin.log 2>&1"
echo [3/14] Admin Server started

ping -n 11 127.0.0.1 >nul

:: Start Auth Service
if not exist "%PROJECT_DIR%\auth-service\target\auth-service-1.0.0.jar" (
    echo    Building auth-service...
    cd /d %PROJECT_DIR%\auth-service
    call mvn clean package -DskipTests
    cd /d %PROJECT_DIR%
)
start /b "" cmd /c "cd /d %PROJECT_DIR%\auth-service && java -jar target\auth-service-1.0.0.jar >%PROJECT_DIR%\logs\auth.log 2>&1"
echo [4/14] Auth Service started

ping -n 11 127.0.0.1 >nul

:: Start Gateway
start /b "" cmd /c "cd /d %PROJECT_DIR%\gateway && java -jar target\gateway-1.0.0.jar >%PROJECT_DIR%\logs\gateway.log 2>&1"
echo [5/14] Gateway started

ping -n 11 127.0.0.1 >nul

:: Start Product Service
start /b "" cmd /c "cd /d %PROJECT_DIR%\backend && java -jar target\product-service-1.0.0.jar >%PROJECT_DIR%\logs\product.log 2>&1"
echo [6/14] Product Service started

:: Start Customer Service
start /b "" cmd /c "cd /d %PROJECT_DIR%\customer-service && java -jar target\customer-service-1.0.0.jar >%PROJECT_DIR%\logs\customer.log 2>&1"
echo [7/14] Customer Service started

:: Start Application Service
start /b "" cmd /c "cd /d %PROJECT_DIR%\application-service && java -jar target\application-service-1.0.0.jar >%PROJECT_DIR%\logs\application.log 2>&1"
echo [8/14] Application Service started

:: Start Underwriting Service
start /b "" cmd /c "cd /d %PROJECT_DIR%\underwriting-service && java -jar target\underwriting-service-1.0.0.jar >%PROJECT_DIR%\logs\underwriting.log 2>&1"
echo [9/14] Underwriting Service started

:: Start Policy Service
start /b "" cmd /c "cd /d %PROJECT_DIR%\policy-service && java -jar target\policy-service-1.0.0.jar >%PROJECT_DIR%\logs\policy.log 2>&1"
echo [10/14] Policy Service started

:: Start Claims Service
start /b "" cmd /c "cd /d %PROJECT_DIR%\claims-service && java -jar target\claims-service-1.0.0.jar >%PROJECT_DIR%\logs\claims.log 2>&1"
echo [11/14] Claims Service started

:: Start Finance Service
start /b "" cmd /c "cd /d %PROJECT_DIR%\finance-service && java -jar target\finance-service-1.0.0.jar >%PROJECT_DIR%\logs\finance.log 2>&1"
echo [12/14] Finance Service started

:: Start Message Service
start /b "" cmd /c "cd /d %PROJECT_DIR%\message-service && java -jar target\message-service-1.0.0.jar >%PROJECT_DIR%\logs\message.log 2>&1"
echo [13/14] Message Service started

:: Start Report Service
start /b "" cmd /c "cd /d %PROJECT_DIR%\report-service && java -jar target\report-service-1.0.0.jar >%PROJECT_DIR%\logs\report.log 2>&1"
echo [14/14] Report Service started

echo.
echo ================================
echo All services starting in background...
echo Check logs in: %PROJECT_DIR%\logs
echo ================================
echo.
echo Access URLs:
echo   Frontend(Nginx): http://localhost
echo   Gateway:         http://localhost:8888
echo   Eureka:          http://localhost:8761
echo   Admin:           http://localhost:8090
echo.
echo Login: admin / 123456
echo ================================
