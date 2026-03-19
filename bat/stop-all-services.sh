#!/bin/bash
# 保险核心系统 - 停止所有服务脚本

echo "================================"
echo "保险核心系统 - 停止所有服务"
echo "================================"
echo ""

# 停止Nginx
echo "停止Nginx..."
pkill -f nginx 2>/dev/null

# 停止所有Java服务 (根据端口)
echo "停止Java服务..."

# 查找并杀死占用以下端口的进程
for port in 8761 8888 8089 8090 8091 8092 8080 8081 8082 8083 8084 8085 8086 8087 8088; do
    pid=$(lsof -t -i:$port 2>/dev/null)
    if [ -n "$pid" ]; then
        echo "  停止端口 $port (PID: $pid)"
        kill -9 $pid 2>/dev/null
    fi
done

echo ""
echo "================================"
echo "所有服务已停止"
echo "================================"
