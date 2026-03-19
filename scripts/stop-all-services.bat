@echo off
echo ========================================
echo   停止所有保险系统服务
echo ========================================
echo.

echo 正在停止 Java 进程...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080 ":8089" ^| findstr "LISTENING"') do (
    echo 终止进程 %%a
    taskkill /F /PID %%a 2>nul
)

for /f "tasks" %%a in ('jps -l ^| findstr "insurance"') do (
    echo 终止 Java 服务: %%a
)

echo.
echo ========================================
echo   所有服务已停止
echo ========================================
pause
