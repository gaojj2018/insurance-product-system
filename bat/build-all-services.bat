@echo off
setlocal enabledelayedexpansion

echo ================================
echo Insurance System - Building All Services
echo ================================

set "PROJECT_DIR=C:\Users\jianjun.gao\insurance-product-system"

cd /d %PROJECT_DIR%

echo.
echo Building all service JARs...
echo.

echo [1/14] Building eureka-server...
call mvn clean package -pl eureka-server -am -DskipTests

echo [2/14] Building config-service...
call mvn clean package -pl config-service -am -DskipTests

echo [3/14] Building admin-server...
call mvn clean package -pl admin-server -am -DskipTests

echo [4/14] Building auth-service...
call mvn clean package -pl auth-service -am -DskipTests

echo [5/14] Building gateway...
call mvn clean package -pl gateway -am -DskipTests

echo [6/14] Building product-service...
call mvn clean package -pl product-service -am -DskipTests

echo [7/14] Building customer-service...
call mvn clean package -pl customer-service -am -DskipTests

echo [8/14] Building application-service...
call mvn clean package -pl application-service -am -DskipTests

echo [9/14] Building underwriting-service...
call mvn clean package -pl underwriting-service -am -DskipTests

echo [10/14] Building policy-service...
call mvn clean package -pl policy-service -am -DskipTests

echo [11/14] Building claims-service...
call mvn clean package -pl claims-service -am -DskipTests

echo [12/14] Building finance-service...
call mvn clean package -pl finance-service -am -DskipTests

REM Note: message-service and report-service are not included in the parent pom
REM Skipping these modules as they are not part of the main build

echo.
echo ================================
echo Build Complete!
echo ================================
echo.
echo JAR files location: target\
echo.
echo Now run: start-services-bg.bat
echo ================================

pause
