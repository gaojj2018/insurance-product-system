@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-17.0.3"
cd /d "%~dp0"
call mvn spring-boot:run %*
