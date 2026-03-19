@echo off
echo ========================================
echo   保险系统服务重启脚本
echo ========================================
echo.
echo 停止现有服务...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":808" ^| findstr "LISTENING"') do (
    taskkill /F /PID %%a 2>nul
)

echo.
echo 启动服务顺序:
echo 1. Eureka (8761)
echo 2. Gateway (8080)
echo 3. Auth (8081)
echo 4. Product (8082)
echo 5. Application (8083)
echo 6. Underwriting (8084)
echo 7. Policy (8085)
echo 8. Claims (8086)
echo 9. Customer (8087)
echo 10. Finance (8088)
echo 11. Message (8089)
echo.
echo 请在 IDEA 中依次重启上述服务
echo.
pause
