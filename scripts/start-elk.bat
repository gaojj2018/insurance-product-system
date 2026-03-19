@echo off
echo ========================================
echo   保险系统 - ELK 一键启动
echo ========================================
echo.

echo [1/3] 启动 Elasticsearch...
start "Elasticsearch" cmd /c "cd /d D:\Program Files\elasticsearch-8.11.0\bin && elasticsearch.bat"
timeout /t 10 /nobreak >nul

echo [2/3] 启动 Kibana...
start "Kibana" cmd /c "cd /d D:\Program Files\kibana-8.11.0\bin && kibana.bat"
timeout /t 5 /nobreak >nul

echo [3/3] 启动 Logstash...
start "Logstash" cmd /c "cd /d D:\Program Files\logstash-8.11.0\bin && logstash.bat -f D:\Program Files\scripts\logstash.conf"

echo.
echo ========================================
echo   启动完成!
echo ========================================
echo Elasticsearch: http://127.0.0.1:9200
echo Kibana:        http://127.0.0.1:5601
echo Logstash:      tcp://127.0.0.1:5044
echo.
pause
