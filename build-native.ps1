# Native Image 构建脚本

Write-Host "========================================"
Write-Host "  GraalVM Native Image Builder"
Write-Host "========================================"
Write-Host ""

# GraalVM 路径
$GRAALVM_HOME = "C:\graalvm\graalvm-community-openjdk-17.0.9+9.1"

Write-Host "GraalVM: $GRAALVM_HOME"
Write-Host ""

# 验证 Native Image
Write-Host "验证 Native Image..."
& "$GRAALVM_HOME\bin\native-image.exe" --version

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Native Image 未找到"
    exit 1
}

Write-Host "✅ Native Image 已找到"
Write-Host ""

# 步骤 1：编译 Java
Write-Host "[1/3] 编译 Java 代码..."

# 创建输出目录
if (Test-Path "target\classes") {
    Remove-Item -Recurse -Force "target\classes"
}
New-Item -ItemType Directory -Path "target\classes" -Force | Out-Null

# 编译
& "$GRAALVM_HOME\bin\javac.exe" -d target/classes src/main/java/com/ocr/jpeg2pdf/SimpleCli.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ 编译失败"
    exit 1
}

Write-Host "✅ 编译成功"
Write-Host ""

# 步骤 2：复制资源
Write-Host "[2/3] 复制资源文件..."

# 创建目录
New-Item -ItemType Directory -Path "target\classes\META-INF\native-image" -Force | Out-Null

# 复制文件
Copy-Item "src\main\resources\META-INF\MANIFEST.MF" "target\classes\META-INF\" -Force
Copy-Item "src\main\resources\META-INF\native-image\native-image.properties" "target\classes\META-INF\native-image\" -Force
Copy-Item "src\main\resources\META-INF\native-image\reflection-config.json" "target\classes\META-INF\native-image\" -Force

Write-Host "✅ 资源文件已复制"
Write-Host ""

# 步骤 3：创建 JAR
Write-Host "[3/3] 创建 JAR..."

Push-Location target\classes
& "$GRAALVM_HOME\bin\jar.exe" cfm ..\simple-cli.jar ..\..\src\main\resources\META-INF\MANIFEST.MF *
Pop-Location

if (-not (Test-Path "target\simple-cli.jar")) {
    Write-Host "❌ JAR 创建失败"
    exit 1
}

Write-Host "✅ JAR 已创建"
Write-Host ""

# 构建 Native Image
Write-Host "========================================"
Write-Host "  构建 Native Image"
Write-Host "========================================"
Write-Host ""
Write-Host "这可能需要 5-10 分钟，请耐心等待..."
Write-Host ""

& "$GRAALVM_HOME\bin\native-image.exe" `
  --no-fallback `
  --enable-http `
  --enable-https `
  -H:IncludeResources=".*\.json$" `
  -H:IncludeResources=".*\.properties$" `
  -H:Name=jpeg2pdf-ofd `
  -jar target/simple-cli.jar

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "❌ 构建失败"
    exit 1
}

Write-Host ""
Write-Host "========================================"
Write-Host "  ✅ 构建成功！"
Write-Host "========================================"
Write-Host ""

# 检查结果
if (Test-Path "jpeg2pdf-ofd.exe") {
    $exe = Get-Item "jpeg2pdf-ofd.exe"
    Write-Host "输出文件: $($exe.Name)"
    Write-Host "文件大小: $([math]::Round($exe.Length/1MB,2)) MB"
    Write-Host "位置: $($exe.FullName)"
    Write-Host ""
    Write-Host "测试运行:"
    Write-Host "  .\jpeg2pdf-ofd.exe --help"
} else {
    Write-Host "❌ 未生成 EXE 文件"
}

Write-Host ""
