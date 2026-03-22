@echo off
chcp 65001 >nul
title JPEG2PDF-OFD Pure Java SE Build

echo ========================================
echo   JPEG2PDF-OFD Pure Java SE Builder
echo ========================================
echo.

REM Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found. Please install JDK 17+
    pause
    exit /b 1
)

echo OK: Java detected
echo.

REM Clean and compile
echo [1/4] Compiling Pure Java SE version...
call mvn clean compile -DskipTests

if errorlevel 1 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo OK: Compilation successful
echo.

REM Create JAR without dependencies
echo [2/4] Creating JAR...

REM Compile PureJavaCli manually
javac -d target/classes -cp "target/classes;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.15.3\jackson-databind-2.15.3.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.15.3\jackson-core-2.15.3.jar;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.15.3\jackson-annotations-2.15.3.jar" src\main\java\com\ocr\jpeg2pdf\PureJavaCli.java

if errorlevel 1 (
    echo ERROR: Manual compilation failed
    pause
    exit /b 1
)

echo OK: PureJavaCli compiled
echo.

REM Package with all dependencies
echo [3/4] Packaging with dependencies...
call mvn package -DskipTests

if errorlevel 1 (
    echo ERROR: Packaging failed
    pause
    exit /b 1
)

echo OK: Packaging successful
echo.

REM Create Pure Java SE distribution
echo [4/4] Creating Pure Java SE distribution...

if exist "dist-pure" rmdir /s /q "dist-pure"
mkdir "dist-pure"

REM Copy JAR
copy "target\jpeg2pdf-ofd-1.0.0.jar" "dist-pure\jpeg2pdf-ofd-pure.jar" >nul

REM Create run script
(
echo @echo off
echo chcp 65001 ^>nul
echo echo.
echo echo ========================================
echo echo   JPEG2PDF-OFD Pure Java SE v1.0.0
echo echo ========================================
echo echo.
echo if "%%1"=="" (
echo     echo 用法: jpeg2pdf-ofd-pure.bat config.json
echo     echo.
echo     echo 選項:
echo     echo   --help    顯示幫助
echo     echo   --version 顯示版本
echo     echo.
echo     pause
echo     exit /b 1
echo ^)
echo.
echo java -Xmx2G -jar jpeg2pdf-ofd-pure.jar %%1
echo.
echo if errorlevel 1 (
echo     echo.
echo     echo ❌ 執行失敗
echo     pause
echo     exit /b 1
echo ^)
echo.
echo pause
) > "dist-pure\jpeg2pdf-ofd-pure.bat"

REM Create README
(
echo # JPEG2PDF-OFD Pure Java SE
echo.
echo ## 特點
echo.
echo - ✅ 無 Spring Boot 框架
echo - ✅ 更輕量（預計 20-30 MB）
echo - ✅ 更快啟動（<1 秒）
echo - ✅ 更少內存（50-100 MB）
echo - ✅ 無 Web Server
echo.
echo ## 使用方法
echo.
echo ```cmd
echo jpeg2pdf-ofd-pure.bat config.json
echo ```
echo.
echo ## 配置示例
echo.
echo ```json
echo {
echo   "input": {
echo     "folder": "C:/OCR/Watch",
echo     "pattern": "*.jpg"
echo   },
echo   "output": {
echo     "folder": "C:/OCR/Output",
echo     "format": "pdf"
echo   },
echo   "ocr": {
echo     "language": "chinese_cht"
echo   }
echo }
echo ```
echo.
echo ## 支持的格式
echo.
echo - pdf - 可搜索 PDF
echo - txt - 純文本
echo - all - 所有格式
echo.
echo ## 支持的語言
echo.
echo - chinese_cht (繁體中文)
echo - ch (簡體中文)
echo - en (英文)
echo - 80+ 種其他語言
) > "dist-pure\README.md"

REM Copy config example
(
echo {
echo   "input": {
echo     "folder": "C:/OCR/Watch",
echo     "pattern": "*.jpg"
echo   },
echo   "output": {
echo     "folder": "C:/OCR/Output",
echo     "format": "pdf"
echo   },
echo   "ocr": {
echo     "language": "chinese_cht"
echo   }
echo }
) > "dist-pure\config-example.json"

echo OK: Distribution created
echo.

REM Display results
echo ========================================
echo   Build Complete!
echo ========================================
echo.
echo Location:
echo   dist-pure\
echo.

REM Calculate folder size
for /f "tokens=3" %%a in ('dir /s dist-pure ^| findstr /i "File(s)"') do (
    set size=%%a
    goto :show_size
)

:show_size
echo Folder size: %size% bytes
echo.
echo ========================================
echo   Usage
echo ========================================
echo.
echo cd dist-pure
echo jpeg2pdf-ofd-pure.bat config-example.json
echo.
echo Requirements:
echo   OK: JDK 17+ (for running JAR)
echo   OK: No Spring Boot needed
echo   OK: No Web Server
echo.
echo ========================================

pause
