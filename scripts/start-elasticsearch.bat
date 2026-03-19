@echo off
echo 启动 Elasticsearch...
cd /d "D:\Program Files\elasticsearch-8.11.0\bin"
start "Elasticsearch" elasticsearch.bat
echo Elasticsearch 已启动 http://127.0.0.1:9200
pause
