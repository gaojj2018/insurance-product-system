@echo off
echo ================================
echo 保险核心系统 - 前端打包部署
echo ================================

cd /d %~dp0frontend

echo.
echo [1/3] 安装依赖...
call npm install

echo.
echo [2/3] 打包前端...
call npm run build

echo.
echo [3/3] 复制到Nginx...
if exist "dist\index.html" (
    if exist "..\nginx\html" (
        rd /s /q "..\nginx\html" 2>nul
        xcopy /e /y /q "dist" "..\nginx\html\"
        echo.
        echo ================================
        echo 部署完成！
        echo 访问地址: http://localhost
        echo ================================
    ) else (
        echo [错误] Nginx目录不存在
    )
) else (
    echo [错误] 打包失败，未生成dist目录
)

pause
