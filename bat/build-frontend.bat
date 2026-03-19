@echo off
chcp 65001 >nul
echo ================================
echo Insurance System - Frontend Build
echo ================================

cd /d %~dp0frontend

echo.
echo [1/3] Installing dependencies...
call npm install

echo.
echo [2/3] Building frontend...
call npm run build

echo.
echo [3/3] Copying to Nginx...
if exist "dist\index.html" (
    if exist "C:\nginx\html" (
        rd /s /q "C:\nginx\html" 2>nul
        xcopy /e /y /q "dist" "C:\nginx\html\"
        echo.
        echo ================================
        echo Build completed!
        echo Access: http://localhost
        echo ================================
    ) else (
        echo [Error] Nginx directory not found
    )
) else (
    echo [Error] Build failed, dist folder not found
)

pause
