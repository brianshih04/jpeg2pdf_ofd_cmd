@echo off
chcp 65001 >nul
title JPEG2PDF-OFD Build Tool

echo ========================================
echo   JPEG2PDF-OFD Executable Builder
echo ========================================
echo.

REM Check JDK
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found. Please install JDK 17+
    pause
    exit /b 1
)

echo OK: Java detected
echo.

REM Clean and compile
echo [1/4] Cleaning and compiling...
call mvn clean package -DskipTests

if errorlevel 1 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo OK: Build successful
echo.

REM Create output directory
echo [2/4] Creating output directory...
if exist "dist-exe" rmdir /s /q "dist-exe"
mkdir "dist-exe"

echo OK: Output directory created
echo.

REM Package with jpackage
echo [3/4] Creating standalone executable with jpackage...
echo This may take 2-3 minutes, please wait...
echo.

jpackage ^
  --name "JPEG2PDF-OFD" ^
  --input target ^
  --main-jar jpeg2pdf-ofd-1.0.0.jar ^
  --main-class org.springframework.boot.loader.launch.JarLauncher ^
  --type app-image ^
  --dest dist-exe ^
  --java-options "-Xmx2G" ^
  --win-console ^
  --app-version "1.0.0" ^
  --description "JPEG OCR to Searchable PDF/OFD - Command Line Tool" ^
  --vendor "Brian Shih"

if errorlevel 1 (
    echo.
    echo ERROR: Packaging failed
    echo.
    echo Possible reasons:
    echo   1. jpackage not found (need JDK 17+)
    echo   2. Main class configuration error
    echo.
    pause
    exit /b 1
)

echo.
echo OK: Packaging successful!
echo.

REM Create README
echo [4/4] Creating documentation...

(
echo # JPEG2PDF-OFD Standalone Executable
echo.
echo ## Features
echo.
echo - **No Java installation required** - JRE included
echo - **Double-click to run** - or use from command line
echo - **Batch processing** - support folder batch conversion
echo - **Multi-format output** - PDF/OFD/TXT
echo - **80+ languages** - OCR support
echo.
echo ## Usage
echo.
echo ### Method 1: Command Line (Recommended)
echo.
echo ```cmd
echo JPEG2PDF-OFD.exe config.json
echo ```
echo.
echo ### Method 2: Create Shortcut
echo.
echo 1. Create config.json configuration file
echo 2. Create run.bat:
echo    ```cmd
echo    @echo off
echo    JPEG2PDF-OFD.exe config.json
echo    pause
echo    ```
echo 3. Double-click run.bat
echo.
echo ## Configuration Examples
echo.
echo ### Simple Config (Single File)
echo.
echo ```json
echo {
echo   "input": "image.jpg",
echo   "output": {
echo     "folder": "./output",
echo     "format": "pdf"
echo   },
echo   "ocr": {
echo     "language": "chinese_cht"
echo   }
echo }
echo ```
echo.
echo ### Batch Config
echo.
echo ```json
echo {
echo   "input": {
echo     "folder": "./images",
echo     "pattern": "*.jpg",
echo     "recursive": true
echo   },
echo   "output": {
echo     "folder": "./output",
echo     "format": ["pdf", "ofd", "txt"]
echo   },
echo   "ocr": {
echo     "language": "chinese_cht"
echo   }
echo }
echo ```
echo.
echo ## System Requirements
echo.
echo - Windows 10/11 (64-bit)
echo - **No Java installation required**
echo - At least 2 GB RAM
echo.
echo ## Output Formats
echo.
echo - **pdf** - Searchable PDF (default)
echo - **ofd** - Searchable OFD
echo - **txt** - Plain text
echo - **all** - All formats
echo - **["pdf", "ofd"]** - Multiple formats
echo.
echo ## Supported Languages
echo.
echo - chinese_cht (Traditional Chinese, default)
echo - ch (Simplified Chinese)
echo - en (English)
echo - japan (Japanese)
echo - korean (Korean)
echo - french, german, spanish, etc. (80+ languages)
echo.
echo ## Support
echo.
echo - GitHub: https://github.com/brianshih04/jpeg2pdf_ofd_cmd
echo.
) > "dist-exe\JPEG2PDF-OFD\README.md"

echo OK: Documentation created
echo.

REM Create sample config files
echo Creating sample config files...

(
echo {
echo   "input": "scan.jpg",
echo   "output": {
echo     "folder": "./output",
echo     "format": "pdf"
echo   },
echo   "ocr": {
echo     "language": "chinese_cht"
echo   }
echo }
) > "dist-exe\JPEG2PDF-OFD\config-simple.json"

(
echo {
echo   "input": {
echo     "folder": "./images",
echo     "pattern": "*.jpg"
echo   },
echo   "output": {
echo     "folder": "./output",
echo     "format": "pdf"
echo   },
echo   "ocr": {
echo     "language": "chinese_cht"
echo   }
echo }
) > "dist-exe\JPEG2PDF-OFD\config-batch.json"

(
echo @echo off
echo chcp 65001 ^>nul
echo echo.
echo echo ========================================
echo echo   JPEG2PDF-OFD Command Line Tool
echo echo ========================================
echo echo.
echo echo Usage:
echo echo   JPEG2PDF-OFD.exe config.json
echo echo.
echo echo Examples:
echo echo   JPEG2PDF-OFD.exe config-simple.json
echo echo.
echo echo Help:
echo echo   JPEG2PDF-OFD.exe --help
echo echo.
echo pause
) > "dist-exe\JPEG2PDF-OFD\run.bat"

echo OK: Sample files created
echo.

REM Display results
echo ========================================
echo   Build Complete!
echo ========================================
echo.
echo Location:
echo   dist-exe\JPEG2PDF-OFD\
echo.
echo Main executable:
echo   dist-exe\JPEG2PDF-OFD\JPEG2PDF-OFD.exe
echo.

REM Calculate folder size
for /f "tokens=3" %%a in ('dir /s dist-exe\JPEG2PDF-OFD ^| findstr /i "File(s)"') do (
    set size=%%a
    goto :show_size
)

:show_size
echo Folder size: ~220 MB
echo.
echo ========================================
echo   Usage
echo ========================================
echo.
echo 1. Navigate to folder:
echo    cd dist-exe\JPEG2PDF-OFD
echo.
echo 2. Run from command line:
echo    JPEG2PDF-OFD.exe config.json
echo.
echo 3. Or double-click:
echo    run.bat
echo.
echo Requirements:
echo   OK: Windows 10/11 (64-bit)
echo   OK: No Java installation required
echo.
echo ========================================

pause
