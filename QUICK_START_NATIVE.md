# 🚀 快速开始：创建单文件 EXE

## 📋 当前状态

```
✅ 简化版代码已创建（SimpleCli.java）
✅ Native Image 配置已准备
✅ 构建脚本已创建（build-native.bat）
⏳ 需要安装 GraalVM
```

---

## 🎯 两种方式

### **方式 A：完整安装（推荐）**

**优点：**
- ✅ 真正的单文件 EXE
- ✅ 最小体积（50-80 MB）
- ✅ 快速启动

**步骤：**
1. 安装 Visual Studio Build Tools
2. 安装 GraalVM
3. 运行 `build-native.bat`
4. 获得 `jpeg2pdf-ofd.exe`

**时间：** 30-45 分钟（首次安装）

---

### **方式 B：使用 Launch4j（快速）**

**优点：**
- ✅ 单文件 EXE
- ✅ 无需 GraalVM
- ✅ 快速创建

**缺点：**
⚠️ 文件较大（200-250 MB）
⚠️ 启动稍慢

**步骤：**
1. 下载 Launch4j
2. 运行 `build-launch4j.bat`
3. 获得 `jpeg2pdf-ofd.exe`

**时间：** 5-10 分钟

---

## 🚀 推荐路径

### **如果你：**

**追求最小体积 → 方式 A（GraalVM）**
```
最终文件：50-80 MB
安装时间：30-45 分钟
```

**快速获得单文件 → 方式 B（Launch4j）**
```
最终文件：200-250 MB
安装时间：5-10 分钟
```

---

## 📝 详细步骤

### **方式 A：GraalVM Native Image**

#### **步骤 1：安装 Visual Studio Build Tools**

```
下载：https://visualstudio.microsoft.com/visual-cpp-build-tools/

安装组件：
  ✅ Desktop development with C++
  ✅ Windows 10 SDK
  
大小：约 6 GB
```

#### **步骤 2：安装 GraalVM**

```
下载：https://github.com/graalvm/graalvm-ce-builds/releases

选择：graalvm-community-jdk-17.0.9_windows-x64_bin.zip

解压到：C:\graalvm\

设置环境变量：
  JAVA_HOME=C:\graalvm\graalvm-community-openjdk-17.0.9+12.1
  PATH=C:\graalvm\...\bin;...

安装 Native Image：
  gu install native-image
```

#### **步骤 3：构建 Native Image**

```cmd
cd D:\Projects\jpeg2pdf_ofd_cmd
build-native.bat
```

#### **步骤 4：测试**

```cmd
jpeg2pdf-ofd.exe config.json
```

---

### **方式 B：Launch4j**

#### **步骤 1：下载 Launch4j**

```
下载：https://launch4j.sourceforge.net/

安装到：C:\Program Files\Launch4j\
```

#### **步骤 2：构建**

```cmd
build-launch4j.bat
```

---

## 📊 对比

| 方式 | 文件大小 | 启动速度 | 安装时间 | 难度 |
|------|---------|---------|---------|------|
| **GraalVM** | 50-80 MB | ⭐⭐⭐⭐⭐ | 30-45 min | ⭐⭐⭐ |
| **Launch4j** | 200-250 MB | ⭐⭐⭐ | 5-10 min | ⭐⭐⭐⭐⭐ |
| **jpackage** | 212 MB | ⭐⭐⭐⭐ | 5 min | ⭐⭐⭐⭐⭐ |

---

## 🎯 我的建议

**如果你现在就要：**
→ 使用 jpackage 版本（已完成）
→ 文件夹：`dist-exe\JPEG2PDF-OFD\`
→ 大小：212 MB

**如果你有时间：**
→ 安装 GraalVM
→ 生成真正的单文件 EXE
→ 大小：50-80 MB

---

## 💡 下一步

1. **选择方式**：GraalVM 或 Launch4j
2. **告诉我你的选择**
3. **我帮你完成剩余步骤**

---

**你想选择哪个？** 🎯✨
