@echo off
echo Formatting code with Spotless...

mvn spotless:apply

echo Code formatting complete.

echo Running Checkstyle...

mvn checkstyle:check

if %errorlevel% neq 0 (
    echo Checkstyle found violations!
    exit /b 1
) else (
    echo Checkstyle passed.
)