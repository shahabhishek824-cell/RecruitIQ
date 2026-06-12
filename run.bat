@echo off
echo Starting RecruitIQ...
echo First run downloads dependencies (2-3 minutes). Please wait.
echo.

SET MVN_VERSION=3.9.6
SET MVN_DIR=%~dp0.mvn\maven
SET MVN_EXE=%MVN_DIR%\apache-maven-%MVN_VERSION%\bin\mvn.cmd

IF NOT EXIST "%MVN_EXE%" (
    echo Downloading Maven, please wait...
    mkdir "%MVN_DIR%" 2>nul
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MVN_VERSION%/apache-maven-%MVN_VERSION%-bin.zip' -OutFile '%MVN_DIR%\maven.zip'"
    powershell -Command "Expand-Archive '%MVN_DIR%\maven.zip' -DestinationPath '%MVN_DIR%' -Force"
    del "%MVN_DIR%\maven.zip"
    echo Maven ready.
    echo.
)

"%MVN_EXE%" spring-boot:run
pause
