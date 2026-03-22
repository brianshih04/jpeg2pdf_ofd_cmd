# GraalVM 快速安装指南

## 🚀 快速安装步骤（10 分钟）

---

### **步骤 1：下载 GraalVM**

**下载地址：**
```
https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-17.0.9/graalvm-community-jdk-17.0.9_windows-x64_bin.zip
```

**或访问：**
```
https://github.com/graalvm/graalvm-ce-builds/releases/tag/jdk-17.0.9
```

---

### **步骤 2：解压到 C:\graalvm**

```powershell
# 创建目录
New-Item -ItemType Directory -Path "C:\graalvm" -Force

# 解压（假设下载到 Downloads 文件夹）
Expand-Archive -Path "$env:USERPROFILE\Downloads\graalvm-community-jdk-17.0.9_windows-x64_bin.zip" -DestinationPath "C:\graalvm"
```

---

### **步骤 3：设置环境变量**

```powershell
# 设置 JAVA_HOME
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\graalvm\graalvm-community-openjdk-17.0.9+12.1', 'User')

# 添加到 PATH
$path = [System.Environment]::GetEnvironmentVariable('PATH', 'User')
$newPath = 'C:\graalvm\graalvm-community-openjdk-17.0.9+12.1\bin;' + $path
[System.Environment]::SetEnvironmentVariable('PATH', $newPath, 'User')

# 重启 PowerShell 使环境变量生效
```

---

### **步骤 4：安装 Native Image**

```powershell
# 使用 GraalVM Updater
gu install native-image

# 验证安装
native-image --version
```

---

### **步骤 5：验证 GraalVM**

```powershell
# 检查 Java 版本
java -version

# 应该显示：
# openjdk version "17.0.9" 2023-10-17
# OpenJDK Runtime Environment GraalVM CE 17.0.9+12.1
# OpenJDK 64-Bit Server VM GraalVM CE 17.0.9+12.1
```

---

## 📋 完整脚本（一键安装）

```powershell
# 1. 下载 GraalVM
$downloadUrl = "https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-17.0.9/graalvm-community-jdk-17.0.9_windows-x64_bin.zip"
$downloadPath = "$env:USERPROFILE\Downloads\graalvm-17.zip"

Write-Host "下载 GraalVM..."
Invoke-WebRequest -Uri $downloadUrl -OutFile $downloadPath

# 2. 解压
Write-Host "解压 GraalVM..."
New-Item -ItemType Directory -Path "C:\graalvm" -Force | Out-Null
Expand-Archive -Path $downloadPath -DestinationPath "C:\graalvm" -Force

# 3. 设置环境变量
Write-Host "设置环境变量..."
$graalvmHome = "C:\graalvm\graalvm-community-openjdk-17.0.9+12.1"
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', $graalvmHome, 'User')

$path = [System.Environment]::GetEnvironmentVariable('PATH', 'User')
$newPath = "$graalvmHome\bin;" + $path
[System.Environment]::SetEnvironmentVariable('PATH', $newPath, 'User')

# 4. 安装 Native Image
Write-Host "安装 Native Image..."
& "$graalvmHome\bin\gu.cmd" install native-image

Write-Host "✅ GraalVM 安装完成！"
Write-Host "请重启 PowerShell 然后运行："
Write-Host "  native-image --version"
```

---

## ⚠️ 注意事项

### **需要 Visual Studio Build Tools**

**下载地址：**
```
https://visualstudio.microsoft.com/visual-cpp-build-tools/
```

**安装步骤：**
1. 下载 "Build Tools for Visual Studio 2022"
2. 运行安装程序
3. 选择 **"Desktop development with C++"**
4. 点击安装（约 6 GB）

**验证安装：**
```powershell
# 应该能找到 vcvarsall.bat
dir "C:\Program Files\Microsoft Visual Studio\2022\BuildTools\VC\Auxiliary\Build\vcvarsall.bat"
```

---

## ✅ 验证安装完成

运行以下命令，应该都成功：

```powershell
# 1. Java 版本
java -version
# 应显示 GraalVM CE 17.0.9

# 2. Native Image 版本
native-image --version
# 应显示 GraalVM 版本号

# 3. Visual Studio 工具
where cl.exe
# 应显示 cl.exe 路径（在 VS 目录下）
```

---

## 📞 下一步

安装完成后，告诉我，我会帮你：

1. ✅ 创建简化版命令行代码
2. ✅ 配置 Native Image
3. ✅ 生成单文件 EXE（50-80 MB）
4. ✅ 测试运行

---

**准备好了告诉我！** 🚀
