@echo off
echo ========================================
echo   Insurance System - Service Starter
echo ========================================
echo.

set JAVA_HOME=C:\Program Files\Java\jdk-17.0.3
set PROJECT_DIR=C:\Users\jianjun.gao\insurance-product-system

echo [1/8] Starting Eureka Server...
start "" "%JAVA_HOME%\bin\java.exe" -jar "%PROJECT_DIR%\eureka-server\target\eureka-server-1.0.0.jar"

echo [2/8] Starting Auth Service on port 8092...
start "" "%JAVA_HOME%\bin\java.exe" -jar "%PROJECT_DIR%\auth-service\target\auth-service-1.0.0.jar"

echo [3/8] Starting Product Service on port 8080...
start "" "%JAVA_HOME%\bin\java.exe" -jar "%PROJECT_DIR%\product-service\target\product-service-1.0.0.jar"

echo [4/8] Starting Customer Service on port 8082...
start "" "%JAVA_HOME%\bin\java.exe" -jar "%PROJECT_DIR%\customer-service\target\customer-service-1.0.0.jar"

echo [5/8] Starting Application Service on port 8083...
start "" "%JAVA_HOME%\bin\java.exe" -jar "%PROJECT_DIR%\application-service\target\application-service-1.0.0.jar"

echo [6/8] Starting Underwriting Service on port 8084...
start "" "%JAVA_HOME%\bin\java.exe" -jar "%PROJECT_DIR%\underwriting-service\target\underwriting-service-1.0.0.jar"

echo [7/8] Starting Policy & Claims Service on port 8085,8086...
start "" "%JAVA_HOME%\bin\java.exe" -jar "%PROJECT_DIR%\policy-service\target\policy-service-1.0.0.jar"
start "" "%JAVA_HOME%\bin\java.exe" -jar "%PROJECT_DIR%\claims-service\target\claims-service-1.0.0.jar"

echo [8/8] Starting Finance Service on port 8087...
start "" "%JAVA_HOME%\bin\java.exe" -jar "%PROJECT_DIR%\finance-service\target\finance-service-1.0.0.jar"

echo Waiting for services to start...
timeout /t 20 /nobreak > nul

echo Rebuilding Gateway with correct routes...
cd "%PROJECT_DIR%\gateway"
call mvn package -DskipTests -q

echo Starting Gateway on port 8888...
start "" "%JAVA_HOME%\bin\java.exe" -jar "%PROJECT_DIR%\gateway\target\gateway-1.0.0.jar"

echo.
echo ========================================
echo   All services started!
echo ========================================
echo.
echo Ports:
echo   - Eureka:      8761
echo   - Gateway:     8888
echo   - Auth:        8092
echo   - Product:     8080
echo   - Customer:    8082
echo   - Application: 8083
echo   - Underwriting: 8084
echo   - Policy:      8085
echo   - Claims:      8086
echo   - Finance:     8087
echo.
echo Frontend: http://localhost
echo.
pause
