@echo off
echo 启动 Logstash...
cd /d "D:\Program Files\logstash-8.11.0\bin"
start "Logstash" logstash.bat -f ..\..\scripts\logstash.conf
echo Logstash 已启动，监听端口 5044
pause
