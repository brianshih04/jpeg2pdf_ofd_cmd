@echo off
chcp 65001 >nul
title GraalVM Native Image Builder

echo ========================================
echo   GraalVM Native Image Builder
echo ========================================
echo.

REM 设置 GraalVM 路径（直接指定，不依赖环境变量）
set GRAALVM_HOME=C:\graalvm\graalvm-community-openjdk-17.0.9+9.1
set PATH=%GRAALVM_HOME%\bin;%PATH%

echo 使用 GraalVM: %GRAALVM_HOME%
echo.

REM 验证 Native Image
echo 验证 Native Image...
"%GRAALVM_HOME%\bin\native-image.exe" --version
if errorlevel 1 (
    echo.
    echo ERROR: Native Image not found
    echo.
    pause
    exit /b 1
)

echo OK: Native Image found
echo.

REM 编译 Java 代码
echo [1/3] 编译 Java 代码...

REM 创建输出目录
if not exist "target\classes" mkdir target\classes

REM 编译
"%GRAALVM_HOME%\bin\javac.exe" -d target/classes src/main/java/com/ocr/jpeg2pdf/SimpleCli.java

if errorlevel 1 (
    echo ERROR: 编译失败
    pause
    exit /b 1
)

echo OK: 编译成功
echo.

REM 复制资源文件
echo [2/3] 复制资源文件...

REM 创建 META-INF 目录
if not exist "target\classes\META-INF\native-image" mkdir target\classes\META-INF\native-image

REM 复制配置文件
copy /y src\main\resources\META-INF\MANIFEST.MF target\classes\META-INF\ >nul
copy /y src\main\resources\META-INF\native-image\native-image.properties target\classes\META-INF\native-image\ >nul
copy /y src\main\resources\META-INF\native-image\reflection-config.json target\classes\META-INF\native-image\ >nul

echo OK: 资源文件已复制
echo.

REM 创建 JAR
echo [3/3] 创建 JAR...
"%GRAALVM_HOME%\bin\jar.exe" cfm target/simple-cli.jar src\main\resources\META-INF\MANIFEST.MF -C target/classes .

if errorlevel 1 (
    echo ERROR: JAR 创建失败
    pause
    exit /b 1
)

echo OK: JAR 已创建
echo.

REM 构建 Native Image
echo ========================================
echo   构建 Native Image
echo ========================================
echo.
echo 这可能需要 5-10 分钟，请耐心等待...
echo.
echo 构建过程中会显示大量日志信息...
echo.

"%GRAALVM_HOME%\bin\native-image.exe" ^
  --no-fallback ^
  --enable-http ^
  --enable-https ^
  -H:IncludeResources=".*\.json$" ^
  -H:IncludeResources=".*\.properties$" ^
  -H:Name=jpeg2pdf-ofd ^
  -jar target/simple-cli.jar

if errorlevel 1 (
    echo.
    echo ========================================
    echo   构建失败
    echo ========================================
    echo.
    echo 可能的原因：
    echo   1. 内存不足（需要 8+ GB RAM）
    echo   2. 缺少 Visual Studio C++ 工具
    echo   3. 配置错误
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   构建成功！
echo ========================================
echo.

REM 检查生成的文件
if exist "jpeg2pdf-ofd.exe" (
    echo 输出文件: jpeg2pdf-ofd.exe
    
    REM 获取文件大小
    for %%A in (jpeg2pdf-ofd.exe) do (
        set size=%%~zA
        set /a sizeMB=!size! / 1048576
        echo 文件大小: !sizeMB! MB
    )
    
    echo.
    echo ========================================
    echo   测试运行
    echo ========================================
    echo.
    echo jpeg2pdf-ofd.exe --help
    echo.
    
) else (
    echo ERROR: 未生成 EXE 文件
)

echo.
pause
