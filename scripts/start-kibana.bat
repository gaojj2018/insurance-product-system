@echo off
echo 启动 Kibana...
cd /d "D:\Program Files\kibana-8.11.0\bin"
start "Kibana" kibana.bat
echo Kibana 已启动 http://127.0.0.1:5601
pause
