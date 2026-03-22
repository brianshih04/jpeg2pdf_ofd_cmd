# GraalVM 自动安装脚本
# 用途：自动解压、配置环境变量、安装 Native Image

Write-Host "========================================"
Write-Host "  GraalVM 安装脚本"
Write-Host "========================================"
Write-Host ""

# 配置
$downloadDir = "$env:USERPROFILE\Downloads\GraalVM"
$zipFile = "$downloadDir\graalvm-17.0.9.zip"
$installDir = "C:\graalvm"
$graalvmHome = "$installDir\graalvm-community-openjdk-17.0.9+12.1"

# 步骤 1：检查下载文件
Write-Host "[1/4] 检查下载文件..."
if (-not (Test-Path $zipFile)) {
    Write-Host "❌ 未找到 GraalVM ZIP 文件"
    Write-Host ""
    Write-Host "请先下载 GraalVM："
    Write-Host "  https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-17.0.9/graalvm-community-jdk-17.0.9_windows-x64_bin.zip"
    Write-Host ""
    Write-Host "下载后保存到："
    Write-Host "  $zipFile"
    Write-Host ""
    pause
    exit 1
}

$fileSize = (Get-Item $zipFile).Length
Write-Host "✅ 找到文件: $([math]::Round($fileSize/1MB,2)) MB"
Write-Host ""

# 步骤 2：解压 GraalVM
Write-Host "[2/4] 解压 GraalVM..."

# 创建安装目录
if (Test-Path $installDir) {
    Write-Host "删除旧的安装目录..."
    Remove-Item -Path $installDir -Recurse -Force
}

New-Item -ItemType Directory -Path $installDir -Force | Out-Null
Write-Host "解压中（约 1-2 分钟）..."

# 解压
Expand-Archive -Path $zipFile -DestinationPath $installDir -Force

Write-Host "✅ 解压完成"
Write-Host "   安装位置: $graalvmHome"
Write-Host ""

# 验证解压
if (-not (Test-Path "$graalvmHome\bin\java.exe")) {
    Write-Host "❌ 解压失败：未找到 java.exe"
    pause
    exit 1
}

# 步骤 3：配置环境变量
Write-Host "[3/4] 配置环境变量..."

# 设置 JAVA_HOME
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', $graalvmHome, 'User')
Write-Host "✅ JAVA_HOME 已设置"
Write-Host "   $graalvmHome"

# 添加到 PATH
$currentPath = [System.Environment]::GetEnvironmentVariable('PATH', 'User')
if ($currentPath -notlike "*$graalvmHome\bin*") {
    $newPath = "$graalvmHome\bin;" + $currentPath
    [System.Environment]::SetEnvironmentVariable('PATH', $newPath, 'User')
    Write-Host "✅ PATH 已更新"
} else {
    Write-Host "✅ PATH 已包含 GraalVM"
}

Write-Host ""

# 步骤 4：安装 Native Image
Write-Host "[4/4] 安装 Native Image..."

# 刷新环境变量
$env:JAVA_HOME = $graalvmHome
$env:PATH = "$graalvmHome\bin;" + $env:PATH

# 运行 gu install
Write-Host "下载并安装 Native Image（约 2-3 分钟）..."
& "$graalvmHome\bin\gu.cmd" install native-image

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Native Image 安装成功"
} else {
    Write-Host "⚠️ Native Image 安装可能失败"
    Write-Host "   请手动运行: gu install native-image"
}

Write-Host ""
Write-Host "========================================"
Write-Host "  ✅ 安装完成！"
Write-Host "========================================"
Write-Host ""
Write-Host "安装信息："
Write-Host "  GraalVM: $graalvmHome"
Write-Host "  Native Image: $graalvmHome\bin\native-image.exe"
Write-Host ""
Write-Host "环境变量："
Write-Host "  JAVA_HOME = $graalvmHome"
Write-Host ""
Write-Host "========================================"
Write-Host "  验证安装"
Write-Host "========================================"
Write-Host ""
Write-Host "请关闭并重新打开 PowerShell，然后运行："
Write-Host ""
Write-Host "  java -version"
Write-Host "  native-image --version"
Write-Host ""
Write-Host "如果显示 GraalVM 版本信息，说明安装成功！"
Write-Host ""

pause
