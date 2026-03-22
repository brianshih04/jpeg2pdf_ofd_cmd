@echo off
chcp 65001 >nul
echo ========================================
echo   JPEG2PDF-OFD CLI 使用說明
echo ========================================
echo.
echo 本程式需要整個資料夾，但包含完整 OCR 功能
echo.
echo 使用方法：
echo.
echo 1. 單文件轉換：
echo    JPEG2PDF-OFD.exe config-single.json
echo.
echo 2. 批量轉換（Watch Folder）：
echo    JPEG2PDF-OFD.exe config-watch.json
echo.
echo 3. 高級配置：
echo    JPEG2PDF-OFD.exe config-advanced.json
echo.
echo ========================================
echo   快速測試
echo ========================================
echo.
echo 將圖片放到 C:\OCR\Watch 資料夾
echo 執行：JPEG2PDF-OFD.exe config-simple.json
echo 輸出：C:\OCR\Output 資料夾
echo.
pause
