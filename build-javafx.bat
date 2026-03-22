@echo off
chcp 65001 >nul
title JPEG2PDF-OFD JavaFX 桌面应用打包工具

echo ========================================
echo   JPEG2PDF-OFD JavaFX 桌面应用打包
echo ========================================
echo.

REM 检查 JDK
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 未检测到 Java，请安装 JDK 17+
    pause
    exit /b 1
)

echo ✅ Java 环境检测通过
echo.

REM 清理并编译
echo [1/4] 清理并编译项目...
call mvn clean package -DskipTests

if errorlevel 1 (
    echo ❌ 编译失败
    pause
    exit /b 1
)

echo ✅ 编译成功
echo.

REM 创建输出目录
echo [2/4] 创建输出目录...
if exist "dist-javafx" rmdir /s /q "dist-javafx"
mkdir "dist-javafx"

echo ✅ 输出目录已创建
echo.

REM 使用 jpackage 打包 JavaFX 桌面应用
echo [3/4] 使用 jpackage 创建 JavaFX 桌面应用...
echo 这可能需要 1-2 分钟，请稍候...
echo.

REM 使用 Spring Boot Fat JAR 直接打包（不使用 jlink）
jpackage ^
  --name "JPEG2PDF-OFD-Desktop" ^
  --input target ^
  --main-jar jpeg2pdf-ofd-1.0.0.jar ^
  --main-class com.ocr.jpeg2pdf.JavaFxDesktopApp ^
  --type app-image ^
  --dest dist-javafx ^
  --java-options "-Xmx2G" ^
  --java-options "--add-exports=javafx.web/com.sun.javafx.webkit=ALL-UNNAMED" ^
  --java-options "--add-exports=javafx.web/com.sun.webkit=ALL-UNNAMED" ^
  --win-console ^
  --app-version "1.0.0" ^
  --description "JPEG OCR to Searchable PDF/OFD - JavaFX Desktop App" ^
  --vendor "Brian Shih"

if errorlevel 1 (
    echo.
    echo ❌ 打包失败
    echo.
    echo 可能的原因：
    echo   1. jpackage 未找到（需要 JDK 17+）
    echo   2. JavaFX 依赖未正确加载
    echo.
    pause
    exit /b 1
)

echo.
echo ✅ 打包成功！
echo.

REM 创建使用说明
echo [4/4] 创建使用说明...

(
echo # JPEG2PDF-OFD JavaFX 桌面应用
echo.
echo ## ✅ 特性
echo.
echo - 独立的 Windows 桌面应用
echo - 使用 JavaFX WebView（WebKit 引擎）
echo - 无需安装浏览器或 Java
echo - 双击运行，自动打开应用窗口
echo - 优雅的加载动画
echo.
echo ## 🚀 使用方法
echo.
echo 1. 双击 JPEG2PDF-OFD-Desktop.exe
echo 2. 等待应用启动（约 20 秒）
echo 3. 应用窗口自动打开
echo 4. 开始使用 OCR 功能
echo.
echo ## 💡 功能
echo.
echo - 📷 OCR 文字识别（80+ 种语言）
echo - 📂 Watch Folder 自动监控
echo - 📄 导出 PDF/OFD/TXT
echo - 🌐 现代化 Web 界面
echo.
echo ## 📂 默认文件夹
echo.
echo - 监控：C:\OCR\Watch\
echo - 输出：C:\OCR\Output\
echo - 保存：C:\OCR\Save\
echo.
echo ## 📋 系统要求
echo.
echo - Windows 10/11 (64-bit)
echo - 无需安装 Java（已包含）
echo - 无需安装浏览器
echo - 至少 2 GB RAM
echo.
echo ## 🎨 界面
echo.
echo - 原生 Windows 窗口
echo - JavaFX WebView（WebKit）
echo - 响应式布局
echo - 加载动画
echo.
echo ## ⚠️ 注意事项
echo.
echo - 首次启动需要 20-30 秒
echo - 需要等待服务完全启动
echo - 关闭窗口会停止服务
echo - 加载屏幕会显示启动进度
echo.
echo ## 🔧 技术栈
echo.
echo - JavaFX 21.0.2
echo - WebView（WebKit）
echo - Spring Boot 3.2.0
echo - RapidOCR（PaddleOCR）
echo - jpackage（JDK 17+）
echo.
echo ## 📊 对比
echo.
echo | 方案 | 文件大小 | 浏览器引擎 | 推荐度 |
echo |------|---------|----------|--------|
echo | **JavaFX** | ~100 MB | WebKit | ⭐⭐⭐⭐⭐ |
echo | JCEF | ~250 MB | Chromium | ⭐⭐⭐ |
echo | SWT | ~220 MB | Edge | ⭐⭐⭐⭐ |
echo.
echo ## 🎯 为什么选择 JavaFX？
echo.
echo 1. ✅ 体积小（100 MB vs 250 MB）
echo 2. ✅ 依赖管理简单
echo 3. ✅ 打包容易
echo 4. ✅ WebKit 对 OCR 应用性能足够
echo 5. ✅ 维护成本低
echo.
echo ## 🐛 故障排除
echo.
echo ### 问题 1：窗口一闪而过
echo - 检查是否安装了 JDK 17+
echo - 使用命令行运行查看错误信息
echo.
echo ### 问题 2：服务启动失败
echo - 检查端口 8000 是否被占用
echo - 检查防火墙设置
echo.
echo ### 问题 3：页面加载失败
echo - 等待服务完全启动（约 20 秒）
echo - 检查杀毒软件是否拦截
echo.
echo ## 📞 支持
echo.
echo - GitHub: https://github.com/brianshih04/JPEG2PDF_OFD_Java_2
echo.
) > "dist-javafx\JPEG2PDF-OFD-Desktop\README.md"

echo ✅ 使用说明已创建
echo.

REM 显示结果
echo ========================================
echo   打包完成！
echo ========================================
echo.
echo 文件位置：
echo   dist-javafx\JPEG2PDF-OFD-Desktop\
echo.
echo 主程序：
echo   dist-javafx\JPEG2PDF-OFD-Desktop\JPEG2PDF-OFD-Desktop.exe
echo.

REM 计算文件夹大小
for /f "tokens=3" %%a in ('dir /s dist-javafx\JPEG2PDF-OFD-Desktop ^| findstr /i "File(s)"') do (
    set size=%%a
    goto :show_size
)

:show_size
echo 文件夹大小：约 200 MB
echo.
echo ========================================
echo   使用方法
echo ========================================
echo.
echo 1. 进入文件夹：
echo    cd dist-javafx\JPEG2PDF-OFD-Desktop
echo.
echo 2. 双击运行：
echo    JPEG2PDF-OFD-Desktop.exe
echo.
echo 3. 等待 20-30 秒，应用窗口会自动打开
echo.
echo 系统要求：
echo   ✅ Windows 10/11 (64-bit)
echo   ✅ 无需安装 Java（已包含）
echo   ✅ 无需安装浏览器
echo.
echo ========================================

pause
