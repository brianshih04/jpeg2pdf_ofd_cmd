# 構建無 Spring Boot 版本

$ErrorActionPreference = "Stop"

Write-Host "========================================"
Write-Host "  JPEG2PDF-OFD No Spring Boot Builder"
Write-Host "========================================"
Write-Host ""

# 1. 備份原始 pom.xml
Write-Host "[1/5] 備份原始 pom.xml..."
Copy-Item "pom.xml" "pom.xml.backup" -Force
Write-Host "  ✅ 已備份到 pom.xml.backup"

# 2. 使用無 Spring Boot 的 pom
Write-Host ""
Write-Host "[2/5] 切換到無 Spring Boot 配置..."
Copy-Item "pom-no-spring.xml" "pom.xml" -Force
Write-Host "  ✅ 已切換到 pom-no-spring.xml"

# 3. 編譯
Write-Host ""
Write-Host "[3/5] 編譯（無 Spring Boot）..."
Write-Host "  這可能需要 1-2 分鐘..."
Write-Host ""

mvn clean package -DskipTests 2>&1 | Out-Null

if ($LASTExitCode -ne 0) {
    Write-Host "  ❌ 編譯失敗"
    Write-Host ""
    Write-Host "恢復原始 pom.xml..."
    Move-Item "pom.xml.backup" "pom.xml" -Force
    exit 1
}

Write-Host "  ✅ 編譯成功"

# 4. 創建分發
Write-Host ""
Write-Host "[4/5] 創建分發..."

if (Test-Path "dist-nospring") {
    Remove-Item -Recurse -Force "dist-nospring"
}

New-Item -ItemType Directory -Path "dist-nospring" -Force | Out-Null
New-Item -ItemType Directory -Path "dist-nospring\lib" -Force | Out-Null

# 複製 JAR
Copy-Item "target\jpeg2pdf-ofd-nospring-3.0.0-jar-with-dependencies.jar" "dist-nospring\jpeg2pdf-ofd-nospring.jar" -Force

# 複製 lib
if (Test-Path "target\lib") {
    Copy-Item -Recurse -Force "target\lib\*" "dist-nospring\lib\"
}

Write-Host "  ✅ 分發已創建"

# 5. 創建配置和腳本
Write-Host ""
Write-Host "[5/5] 創建配置和腳本..."

# 創建配置
$config = @{
    input = @{
        folder = "C:/OCR/Watch"
        pattern = "*.jpg"
    }
    output = @{
        folder = "C:/OCR/Output"
        format = "pdf"
    }
    ocr = @{
        language = "chinese_cht"
    }
}

$config | ConvertTo-Json -Depth 3 | Set-Content -Path "dist-nospring\config.json" -Encoding UTF8

# 創建運行腳本
$runScript = @'
@echo off
chcp 65001 >nul
echo ========================================
echo   JPEG2PDF-OFD No Spring Boot v3.0.0
echo   Pure Java SE Edition
echo ========================================
echo.
if "%1"=="" (
    echo Usage: jpeg2pdf-ofd-nospring.jar config.json
    echo.
    echo Options:
    echo   --help     Show help
    echo   --version  Show version
    echo.
    pause
    exit /b 1
)

java -Xmx2G -jar jpeg2pdf-ofd-nospring.jar %1

if errorlevel 1 (
    echo.
    echo Execution failed
    pause
    exit /b 1
)
'@

$runScript | Out-File -FilePath "dist-nospring\run.bat" -Encoding ASCII

# 創建 README
$readme = @'
# JPEG2PDF-OFD No Spring Boot Edition

## 特點

- ✅ 無 Spring Boot 框架
- ✅ 無 Web Server
- ✅ 更小的文件大小（預計 20-30 MB）
- ✅ 更快的啟動速度（<1 秒）
- ✅ 更少的內存使用（50-80 MB）
- ✅ 純 Java SE 實現

## 對比

| 版本 | JAR 大小 | 啟動時間 | 內存使用 |
|------|---------|---------|---------|
| Spring Boot | 83 MB | ~1.5s | ~150 MB |
| **No Spring** | **~30 MB** | **<1s** | **~80 MB** |

## 使用方法

```cmd
run.bat config.json
```

或直接運行：

```cmd
java -Xmx2G -jar jpeg2pdf-ofd-nospring.jar config.json
```

## 配置

編輯 `config.json` 文件：

```json
{
  "input": {
    "folder": "C:/OCR/Watch",
    "pattern": "*.jpg"
  },
  "output": {
    "folder": "C:/OCR/Output",
    "format": "pdf"
  },
  "ocr": {
    "language": "chinese_cht"
  }
}
```

## 系統要求

- JDK 17+
- Windows 10/11 (64-bit)
- 至少 2 GB RAM

## 版本

- Branch: no-springboot (實驗中)
- Version: 3.0.0
- Framework: None (Pure Java SE)
'@

Set-Content -Path "dist-nospring\README.md" -Encoding UTF8

Write-Host "  ✅ 配置和腳本已創建"

# 恢復原始 pom.xml
Write-Host ""
Write-Host "恢復原始 pom.xml..."
Move-Item "pom.xml.backup" "pom.xml" -Force
Write-Host "  ✅ 已恢復"

# 顯示結果
Write-Host ""
Write-Host "========================================"
Write-Host "  構建完成！"
Write-Host "========================================"
Write-Host ""

# 計算大小
$jar = Get-Item "dist-nospring\jpeg2pdf-ofd-nospring.jar"
$jarSize = [math]::Round($jar.Length / 1MB, 2)

Write-Host "位置："
Write-Host "  dist-nospring\"
Write-Host ""
Write-Host "JAR 大小："
Write-Host "  jpeg2pdf-ofd-nospring.jar: $jarSize MB"
Write-Host ""

if (Test-Path "dist-nospring\lib") {
    $libSize = (Get-ChildItem "dist-nospring\lib" -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB
    $libSizeMB = [math]::Round($libSize, 2)
    Write-Host "依賴庫大小："
    Write-Host "  lib\: $libSizeMB MB"
    Write-Host ""
    
    $totalSize = [math]::Round($jarSize + $libSize, 2)
    Write-Host "總大小："
    Write-Host "  $totalSize MB"
    Write-Host ""
}

Write-Host "========================================"
Write-Host "  使用方法"
Write-Host "========================================"
Write-Host ""
Write-Host "cd dist-nospring"
Write-Host "run.bat config.json"
Write-Host ""
Write-Host "或："
Write-Host ""
Write-Host "java -Xmx2G -jar jpeg2pdf-ofd-nospring.jar config.json"
Write-Host ""
Write-Host "========================================"
Write-Host "  優勢"
Write-Host "========================================"
Write-Host ""
Write-Host "✅ 無 Spring Boot（更小、更快）"
Write-Host "✅ 無框架依賴（更簡單）"
Write-Host "✅ 純 Java SE（更可靠）"
Write-Host ""
Write-Host "========================================"

pause
