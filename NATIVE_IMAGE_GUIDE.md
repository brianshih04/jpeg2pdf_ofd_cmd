# GraalVM Native Image 单文件 EXE 打包指南

## 🎯 目标

创建真正的单文件 EXE，无需其他依赖文件。

---

## 📋 系统要求

- Windows 10/11 (64-bit)
- Visual Studio Build Tools (C++ 开发工具)
- GraalVM JDK 17+
- 至少 8 GB RAM

---

## 🚀 步骤 1：安装 Visual Studio Build Tools

### 下载地址
```
https://visualstudio.microsoft.com/visual-cpp-build-tools/
```

### 安装步骤
1. 下载 "Build Tools for Visual Studio 2022"
2. 运行安装程序
3. 选择 "Desktop development with C++"
4. 安装（约 6 GB）

---

## 🔧 步骤 2：安装 GraalVM

### 方式 A：使用 SDKMAN（推荐）

```powershell
# 1. 安装 SDKMAN
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
Invoke-RestMethod -Uri 'https://get.sdkman.io' | Invoke-Expression

# 2. 重启 PowerShell

# 3. 安装 GraalVM
sdk install java 17.0.9-graal
sdk use java 17.0.9-graal

# 4. 安装 Native Image
gu install native-image
```

---

### 方式 B：手动安装

#### 1. 下载 GraalVM
```
https://github.com/graalvm/graalvm-ce-builds/releases

选择：
- graalvm-community-jdk-17.0.9_windows-x64_bin.zip
```

#### 2. 解压
```powershell
# 解压到 C:\graalvm
Expand-Archive -Path graalvm-community-jdk-17.0.9_windows-x64_bin.zip -DestinationPath C:\
Rename-Item -Path "C:\graalvm-community-openjdk-17.0.9+12.1" -NewName "graalvm"
```

#### 3. 设置环境变量
```powershell
# 设置 JAVA_HOME
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\graalvm', 'User')

# 添加到 PATH
$path = [System.Environment]::GetEnvironmentVariable('PATH', 'User')
$newPath = 'C:\graalvm\bin;' + $path
[System.Environment]::SetEnvironmentVariable('PATH', $newPath, 'User')

# 重启 PowerShell
```

#### 4. 安装 Native Image
```powershell
# 使用 gu (GraalVM Updater)
gu install native-image
```

---

## 🏗️ 步骤 3：修改项目配置

### 1. 添加 Native Image 配置

#### 创建 `native-image.properties`

**位置：** `src/main/resources/META-INF/native-image/`

**内容：**
```properties
Args = --no-fallback \
       --enable-http \
       --enable-https \
       --report-unsupported-elements-at-runtime \
       --initialize-at-build-time=org.slf4j \
       -H:IncludeResources=".*\\.json$" \
       -H:IncludeResources=".*\\.yml$" \
       -H:IncludeResources=".*\\.properties$" \
       --allow-incomplete-classpath
```

---

#### 创建 `reflection-config.json`

**位置：** `src/main/resources/META-INF/native-image/`

**内容：**
```json
[
  {
    "name": "com.ocr.jpeg2pdf.CliApplication",
    "allDeclaredConstructors": true,
    "allPublicConstructors": true,
    "allDeclaredMethods": true,
    "allPublicMethods": true
  },
  {
    "name": "com.ocr.jpeg2pdf.model.OcrResult",
    "allDeclaredConstructors": true,
    "allDeclaredFields": true,
    "allDeclaredMethods": true
  },
  {
    "name": "com.ocr.jpeg2pdf.model.OcrResult$TextPosition",
    "allDeclaredConstructors": true,
    "allDeclaredFields": true,
    "allDeclaredMethods": true
  },
  {
    "name": "com.ocr.jpeg2pdf.model.OcrResult$Point",
    "allDeclaredConstructors": true,
    "allDeclaredFields": true,
    "allDeclaredMethods": true
  },
  {
    "name": "com.fasterxml.jackson.databind.ObjectMapper",
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true
  }
]
```

---

### 2. 简化代码（移除 Spring Boot）

**问题：** Spring Boot 与 Native Image 兼容性复杂

**解决方案：** 创建简化版本（纯 Java）

#### 创建 `SimpleCliApplication.java`

**特点：**
- 不使用 Spring Boot
- 纯 Java + Jackson
- 手动初始化 OCR

---

## 🛠️ 步骤 4：编译 Native Image

### 1. 编译 JAR
```powershell
mvn clean package -DskipTests
```

### 2. 生成 Native Image
```powershell
# 方式 A：使用 Maven 插件
mvn -Pnative package

# 方式 B：直接使用 native-image
native-image `
  --no-fallback `
  --enable-http `
  --enable-https `
  -H:Name=jpeg2pdf-ofd `
  -jar target/jpeg2pdf-ofd-1.0.0.jar
```

---

## 📊 预期结果

```
✅ jpeg2pdf-ofd.exe (50-80 MB)
   
   - 真正的单文件
   - 无需 JRE
   - 无需其他文件
   - 双击即可运行
```

---

## ⚠️ 已知限制

### Spring Boot 兼容性
```
❌ Spring Boot 3.x 与 Native Image 兼容性复杂
✅ 需要大量配置和测试
⏱️ 可能需要 2-4 小时调试
```

### 更简单的替代方案
```
创建纯 Java 版本（无 Spring Boot）
→ 更容易编译成 Native Image
→ 更小的文件体积
```

---

## 🎯 推荐实施路径

### **路径 A：快速验证（推荐）**

```
1. 创建简化版本（无 Spring Boot）
2. 使用 GraalVM Native Image
3. 生成单文件 EXE (50-80 MB)
4. 时间：1-2 小时
```

---

### **路径 B：完整迁移**

```
1. 保持 Spring Boot
2. 添加 Native Image 配置
3. 调试兼容性问题
4. 生成单文件 EXE (100-150 MB)
5. 时间：4-8 小时
```

---

## 💡 我的建议

### **立即开始：创建简化版本**

**优点：**
- ✅ 快速（1-2 小时）
- ✅ 更小的 EXE (50-80 MB)
- ✅ 更稳定
- ✅ 更容易调试

**步骤：**
1. 创建 `SimpleCliApplication.java`（纯 Java）
2. 手动初始化 OCR（不使用 Spring）
3. 使用 GraalVM Native Image
4. 生成单文件 EXE

---

## 🚀 准备好了吗？

**我可以：**

**A. 帮你安装 GraalVM**（5 分钟）
   - 下载 GraalVM
   - 配置环境变量
   - 安装 Native Image

**B. 创建简化版本代码**（30 分钟）
   - SimpleCliApplication.java
   - 手动 OCR 初始化
   - JSON 配置解析

**C. 生成 Native Image**（20 分钟）
   - 编译配置
   - 生成 EXE
   - 测试运行

---

**你想先做哪个？** 🎯✨
