@echo off
setlocal enabledelayedexpansion

echo ================================
echo Insurance System - Restarting All Services
echo ================================

echo.
echo Step 1: Stopping all services...
echo ================================
call "%~dp0stop-all-services.bat"

echo.
echo Waiting for processes to terminate...
ping -n 6 127.0.0.1 >nul

echo.
echo Step 2: Starting all services...
echo ================================
call "%~dp0start-services-bg.bat"
