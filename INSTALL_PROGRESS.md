# 🚀 GraalVM 安装 - 3 步快速指南

## 📋 安装进度

```
⏳ 步骤 1：下载 GraalVM（进行中）
⏸️ 步骤 2：解压 GraalVM
⏸️ 步骤 3：配置环境变量
⏸️ 步骤 4：安装 Native Image
```

---

## 🎯 接下来的步骤

### **步骤 1：等待下载完成**

```
下载正在进行中...
文件大小：约 300 MB
预计时间：5-10 分钟
```

**下载完成后，文件位置：**
```
C:\Users\Brian\Downloads\GraalVM\graalvm-17.0.9.zip
```

---

### **步骤 2：运行安装脚本**

下载完成后，在 PowerShell 中运行：

```powershell
cd D:\Projects\jpeg2pdf_ofd_cmd
.\install-graalvm.ps1
```

**这个脚本会：**
1. ✅ 解压 GraalVM 到 `C:\graalvm\`
2. ✅ 设置 JAVA_HOME 环境变量
3. ✅ 添加到 PATH
4. ✅ 安装 Native Image

**时间：** 约 5 分钟

---

### **步骤 3：验证安装**

**关闭并重新打开 PowerShell**，然后运行：

```powershell
# 检查 Java 版本
java -version

# 应该显示：
# openjdk version "17.0.9" 2023-10-17
# OpenJDK Runtime Environment GraalVM CE 17.0.9+12.1
# OpenJDK 64-Bit Server VM GraalVM CE 17.0.9+12.1
```

```powershell
# 检查 Native Image
native-image --version

# 应该显示 GraalVM 版本号
```

---

### **步骤 4：构建单文件 EXE**

安装成功后，运行：

```powershell
cd D:\Projects\jpeg2pdf_ofd_cmd
.\build-native.bat
```

**时间：** 约 5-10 分钟（首次构建）

**结果：**
```
✅ jpeg2pdf-ofd.exe (50-80 MB)
   - 真正的单文件
   - 无需 JRE
   - 无需其他文件
```

---

## ⚠️ 故障排除

### **问题 1：下载失败**

**解决方案：**
```
手动下载：
https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-17.0.9/graalvm-community-jdk-17.0.9_windows-x64_bin.zip

保存到：
C:\Users\Brian\Downloads\GraalVM\graalvm-17.0.9.zip
```

---

### **问题 2：解压失败**

**原因：** 权限不足

**解决方案：**
```
以管理员身份运行 PowerShell
```

---

### **问题 3：环境变量未生效**

**解决方案：**
```
1. 完全关闭所有 PowerShell 窗口
2. 重新打开 PowerShell
3. 运行：java -version
```

---

### **问题 4：Native Image 构建失败**

**可能原因：**
1. ❌ 未安装 Visual Studio Build Tools
2. ❌ 内存不足（需要 8+ GB RAM）

**解决方案：**
```
安装 Visual Studio Build Tools：
https://visualstudio.microsoft.com/visual-cpp-build-tools/

选择组件：
✅ Desktop development with C++
```

---

## 📊 预期时间表

| 步骤 | 时间 | 状态 |
|------|------|------|
| 下载 GraalVM | 5-10 min | ⏳ 进行中 |
| 运行安装脚本 | 5 min | ⏸️ 等待 |
| 验证安装 | 1 min | ⏸️ 等待 |
| 构建 Native Image | 5-10 min | ⏸️ 等待 |
| **总计** | **15-25 min** | |

---

## 📞 需要帮助？

如果遇到问题：
1. 查看 `INSTALL_GRAALVM.md` 详细指南
2. 查看 `NATIVE_IMAGE_GUIDE.md` 完整文档
3. 告诉我具体的错误信息

---

## ✅ 下一步

**下载完成后，告诉我，我会帮你：**
1. 运行安装脚本
2. 验证安装
3. 构建单文件 EXE

---

**等待下载中...** ⏳✨
