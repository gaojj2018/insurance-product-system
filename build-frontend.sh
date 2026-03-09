#!/bin/bash
# 保险核心系统 - 前端打包部署脚本

echo "================================"
echo "保险核心系统 - 前端打包部署"
echo "================================"
echo ""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/frontend"

echo "[1/3] 安装依赖..."
npm install

echo ""
echo "[2/3] 打包前端..."
npm run build

echo ""
echo "[3/3] 复制到Nginx..."
if [ -f "dist/index.html" ]; then
    rm -rf /c/nginx/html/
    mkdir -p /c/nginx/html
    cp -r dist/* /c/nginx/html/
    echo ""
    echo "================================"
    echo "部署完成！"
    echo "访问地址: http://localhost"
    echo "================================"
else
    echo "[错误] 打包失败，未生成dist目录"
fi
