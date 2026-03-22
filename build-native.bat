@echo off
chcp 65001 >nul
title Native Image Build Tool

echo ========================================
echo   GraalVM Native Image Builder
echo ========================================
echo.

REM Check GraalVM
where native-image >nul 2>&1
if errorlevel 1 (
    echo ERROR: native-image not found
    echo.
    echo Please install GraalVM and Native Image first.
    echo See: INSTALL_GRAALVM.md
    echo.
    pause
    exit /b 1
)

echo OK: GraalVM Native Image found
echo.

REM Check Visual Studio
if not exist "C:\Program Files\Microsoft Visual Studio\2022" (
    if not exist "C:\Program Files (x86)\Microsoft Visual Studio\2022" (
        echo ERROR: Visual Studio Build Tools not found
        echo.
        echo Please install Visual Studio Build Tools with C++ tools.
        echo See: INSTALL_GRAALVM.md
        echo.
        pause
        exit /b 1
    )
)

echo OK: Visual Studio found
echo.

REM Compile Java
echo [1/3] Compiling Java code...
javac -d target/classes src/main/java/com/ocr/jpeg2pdf/SimpleCli.java

if errorlevel 1 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo OK: Compilation successful
echo.

REM Create JAR
echo [2/3] Creating JAR file...

REM Copy resources
xcopy /s /y src\main\resources\* target\classes\

REM Create JAR
jar cfm target/simple-cli.jar src\main\resources\META-INF\MANIFEST.MF -C target/classes .

if errorlevel 1 (
    echo ERROR: JAR creation failed
    pause
    exit /b 1
)

echo OK: JAR created
echo.

REM Build Native Image
echo [3/3] Building Native Image...
echo This may take 5-10 minutes, please wait...
echo.

native-image ^
  --no-fallback ^
  --enable-http ^
  --enable-https ^
  -H:IncludeResources=".*\\.json$" ^
  -H:IncludeResources=".*\\.properties$" ^
  -H:Name=jpeg2pdf-ofd ^
  -jar target/simple-cli.jar

if errorlevel 1 (
    echo.
    echo ERROR: Native Image build failed
    echo.
    echo Possible reasons:
    echo   1. Missing Visual Studio C++ tools
    echo   2. Insufficient memory (need 8+ GB RAM)
    echo   3. Missing reflection configuration
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Build Complete!
echo ========================================
echo.
echo Output: jpeg2pdf-ofd.exe
echo.

REM Check file size
for %%A in (jpeg2pdf-ofd.exe) do (
    echo Size: %%~zA bytes
)

echo.
echo Usage:
echo   jpeg2pdf-ofd.exe config.json
echo.
echo   jpeg2pdf-ofd.exe --help
echo.

pause
