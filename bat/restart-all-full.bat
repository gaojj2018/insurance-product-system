@echo off
setlocal enabledelayedexpansion

echo ================================
echo Insurance System - Full Rebuild & Restart
echo ================================

echo.
echo Step 1: Stopping all services...
echo ================================
call "%~dp0stop-all-services.bat"

echo.
echo Waiting for processes to terminate...
ping -n 6 127.0.0.1 >nul

echo.
echo Step 2: Building all services in parallel...
echo ================================
echo Building frontend and backend services...

start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\frontend && npm run build > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\claims-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\product-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\policy-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\customer-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\application-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\underwriting-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\finance-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\message-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\report-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\auth-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\admin-server && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\gateway && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\config-service && call mvn package -DskipTests -q > nul 2>&1"
start /b cmd /c "cd /d C:\Users\jianjun.gao\insurance-product-system\eureka-server && call mvn package -DskipTests -q > nul 2>&1"

echo Waiting for builds to complete (auto-checking)...
set /a wait_count=0

:wait_loop
ping -n 6 127.0.0.1 >nul
set /a wait_count+=5

set "all_done=1"

if not exist "C:\Users\jianjun.gao\insurance-product-system\frontend\dist\index.html" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\claims-service\target\claims-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\product-service\target\product-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\policy-service\target\policy-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\customer-service\target\customer-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\application-service\target\application-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\underwriting-service\target\underwriting-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\finance-service\target\finance-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\message-service\target\message-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\report-service\target\report-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\auth-service\target\auth-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\admin-server\target\admin-server-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\gateway\target\gateway-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\config-service\target\config-service-1.0.0.jar" set "all_done=0"
if not exist "C:\Users\jianjun.gao\insurance-product-system\eureka-server\target\eureka-server-1.0.0.jar" set "all_done=0"

if %all_done%==0 goto wait_loop

echo All builds completed in %wait_count% seconds.

echo.
echo Step 3: Copying frontend to nginx...
echo ================================
powershell -Command "Copy-Item -Path 'C:\Users\jianjun.gao\insurance-product-system\frontend\dist\*' -Destination 'C:\nginx\html\' -Recurse -Force"

echo.
echo Step 4: Starting all services...
echo ================================
call "%~dp0start-services-bg.bat"

echo.
echo ================================
echo Full Rebuild Complete!
echo ================================
