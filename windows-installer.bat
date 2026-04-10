@echo off
setlocal
title Moodle 2.0 Updater ^& Launcher
echo ======================================
echo    Moodle 2.0 Updater ^& Launcher
echo ======================================
echo.

set "REPO_URL=https://github.com/mdraihankabirsifat/Moodle-2.0.git"
set "BRANCH=main"
set "INSTALL_DIR=%USERPROFILE%\.moodle-app-v2"

:: Check for Git
where git >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Git is not installed. Please install Git for Windows.
    pause
    exit /b 1
)

:: Check for Java
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    if "%JAVA_HOME%"=="" (
        echo [ERROR] Java is not installed or not in PATH.
        echo Please install Java (JDK) to run this application.
        pause
        exit /b 1
    )
)

:: Clone or Update the repository
if not exist "%INSTALL_DIR%" (
    echo [INFO] App not found locally. Downloading the latest version from GitHub...
    git clone --branch "%BRANCH%" "%REPO_URL%" "%INSTALL_DIR%"
) else (
    echo [INFO] Updating to the latest version...
    cd /d "%INSTALL_DIR%"
    git fetch --all
    git reset --hard origin/%BRANCH%
)

:: Navigate to directory and run
cd /d "%INSTALL_DIR%"

echo [INFO] Launching Moodle 2.0...
call mvnw.cmd clean javafx:run

echo Session ended.
pause
