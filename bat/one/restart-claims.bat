@echo off
echo ================================
echo Claims Service - Restarting
echo ================================

echo Step 1: Stopping Claims Service...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8086" ^| findstr LISTENING') do (
    taskkill /F /PID %%a >nul 2>&1
)
echo Claims service stopped.

echo Step 2: Building Claims Service...
cd /d C:\Users\jianjun.gao\insurance-product-system\claims-service
call mvn package -DskipTests

echo Step 3: Starting Claims Service...
start /b cmd /c "java -jar target\claims-service-1.0.0.jar > ..\logs\claims.log 2>&1"

echo ================================
echo Claims Service Restart Complete
echo ================================
