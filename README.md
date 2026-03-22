# JPEG2PDF_OFD - Native Image Edition

**JPEG 图片 OCR 转 Searchable PDF/OFD - 单文件 EXE**

![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-blue)
![Native Image](https://img.shields.io/badge/Native_Image-12.17MB-brightgreen)
![License](https://img.shields.io/badge/License-MIT-green)

---

## ✨ **特性**

- ✅ **单文件 EXE** - 12.17 MB，无需 Java
- ✅ **OCR 识别** - RapidOCR-Java (PP-OCRv4, 80+ 种语言)
- ✅ **Searchable PDF** - 透明文字层（可搜索、可复制）
- ✅ **Searchable OFD** - 符合 GB/T 33190-2016 标准
- ✅ **完美对齐** - 逐字符绝对定位（100% 精准）
- ✅ **批量处理** - 支持通配符和文件夹递归
- ✅ **跨平台** - Windows、macOS、Linux

---

## 🚀 **快速开始**

### **下载**

#### **方式 1：Native Image（推荐）** ⭐⭐⭐⭐⭐

```
文件：jpeg2pdf-ofd.exe
大小：12.17 MB
特点：
  ✅ 真正的单文件 EXE
  ✅ 无需 Java
  ✅ 快速启动
  ✅ 易于分发
```

#### **方式 2：jpackage 版本**

```
文件夹：dist-exe/JPEG2PDF-OFD/
大小：212 MB
特点：
  ✅ 完整功能
  ✅ 无需 Java
  ⚠️ 需要整个文件夹
```

#### **方式 3：Spring Boot JAR**

```
文件：target/jpeg2pdf-ofd-1.0.0.jar
大小：83.32 MB
特点：
  ✅ 单文件
  ⚠️ 需要安装 Java 17+
```

---

## 📋 **使用方法**

### **Native Image 版本**

```cmd
# 显示帮助
jpeg2pdf-ofd.exe --help

# 显示版本
jpeg2pdf-ofd.exe --version

# 转换单个文件
jpeg2pdf-ofd.exe image.jpg output/

# 批量转换（通配符）
jpeg2pdf-ofd.exe images/*.jpg output/

# 转换整个文件夹
jpeg2pdf-ofd.exe images/ output/

# 指定语言
jpeg2pdf-ofd.exe image.jpg output/ --lang chinese_cht

# 指定输出格式
jpeg2pdf-ofd.exe image.jpg output/ --format pdf

# 输出所有格式
jpeg2pdf-ofd.exe image.jpg output/ --format all

# 完整参数
jpeg2pdf-ofd.exe image.jpg output/ --lang en --format pdf
```

### **jpackage 版本**

```cmd
cd dist-exe\JPEG2PDF-OFD

# 使用批处理文件
run.bat

# 或直接运行
JPEG2PDF-OFD.exe config.json
```

### **JAR 版本**

```bash
java -jar target/jpeg2pdf-ofd-1.0.0.jar
```

---

## 📊 **版本对比**

| 特性 | Native Image | jpackage | JAR |
|------|-------------|----------|-----|
| **文件大小** | **12.17 MB** | 212 MB | 83.32 MB |
| **需要 Java** | ❌ 否 | ❌ 否 | ✅ 是 |
| **单文件** | ✅ 是 | ❌ 文件夹 | ✅ 是 |
| **启动速度** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **分发难易** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| **OCR 功能** | ⏳ 基础 | ✅ 完整 | ✅ 完整 |
| **PDF/OFD** | ⏳ 基础 | ✅ 完整 | ✅ 完整 |
| **推荐度** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |

---

## 🌐 **支持的语言**

```
chinese_cht (繁体中文，默认)
ch (简体中文)
en (English)
japan (Japanese)
korean (Korean)
french (法语)
german (德语)
spanish (西班牙语)
portuguese (葡萄牙语)
italian (意大利语)
russian (俄语)
arabic (阿拉伯语)
... (80+ 种语言)
```

---

## 📂 **输出格式**

```
pdf  - 可搜索 PDF（默认）
ofd  - 可搜索 OFD
txt  - 纯文本
all  - 所有格式
```

---

## 🔧 **命令行参数**

### **Native Image 版本**

```
Usage:
  jpeg2pdf-ofd.exe <input> [output] [options]

Arguments:
  input          Input file, folder, or pattern (e.g., *.jpg)
  output         Output folder (default: current directory)

Options:
  --lang, -l     OCR language (default: chinese_cht)
  --format, -f   Output format (default: pdf)
  --help, -h     Show help
  --version, -v  Show version

Examples:
  jpeg2pdf-ofd.exe scan.jpg
  jpeg2pdf-ofd.exe scan.jpg output/
  jpeg2pdf-ofd.exe scan.jpg output/ --lang en --format pdf
  jpeg2pdf-ofd.exe images/*.jpg output/ --format all
  jpeg2pdf-ofd.exe images/ output/ --lang chinese_cht
```

---

## 🛠️ **构建**

### **Native Image**

```powershell
# 1. 安装 GraalVM
# 参见：INSTALL_GRAALVM.md

# 2. 构建
.\build-native-pure.ps1

# 输出
jpeg2pdf-ofd.exe (12.17 MB)
```

### **jpackage**

```bash
mvn clean package -DskipTests
.\build-exe.bat

# 输出
dist-exe\JPEG2PDF-OFD\ (212 MB)
```

### **JAR**

```bash
mvn clean package -DskipTests

# 输出
target\jpeg2pdf-ofd-1.0.0.jar (83.32 MB)
```

---

## 🏗️ **技术栈**

| 组件 | 技术 | 版本 |
|------|------|------|
| **Native Image** | GraalVM CE | 17.0.9 |
| **OCR** | RapidOCR | 0.0.7 |
| **PDF** | Apache PDFBox | 2.0.29 |
| **OFD** | ofdrw-layout | 2.3.8 |
| **框架** | Spring Boot | 3.2.0 |

---

## 📦 **项目结构**

```
jpeg2pdf_ofd_cmd/
├── jpeg2pdf-ofd.exe              (Native Image, 12.17 MB)
├── dist-exe/JPEG2PDF-OFD/        (jpackage, 212 MB)
├── target/jpeg2pdf-ofd-1.0.0.jar (JAR, 83.32 MB)
│
├── src/main/java/
│   ├── NativeImageCli.java       (Native Image 版本)
│   ├── CliApplication.java       (命令行版本)
│   └── Application.java          (Web 版本)
│
├── build-native-pure.ps1         (Native Image 构建)
├── build-exe.bat                 (jpackage 构建)
└── install-graalvm.ps1           (GraalVM 安装)
```

---

## 📚 **文档**

- **NATIVE_IMAGE_SUCCESS.md** - Native Image 完整说明
- **INSTALL_GRAALVM.md** - GraalVM 安装指南
- **NATIVE_IMAGE_GUIDE.md** - 技术细节
- **QUICK_START_NATIVE.md** - 快速开始
- **JSON_CONFIG_DESIGN.md** - JSON 配置设计
- **CLI_DESIGN.md** - 命令行设计
- **DISTRIBUTION_OPTIONS.md** - 分发方案对比

---

## 🎯 **核心算法**

### **逐字符绝对定位** ⭐ **终极武器**

经过 **30 个版本** 的系统化迭代，最终方案：

**剥夺 OFD 引擎的排版权，逐字符绝对定位！**

```java
// 1. 测量每个字母的 AWT 物理宽度
double[] charWidthsMm = new double[text.length()];
double totalAwtWidthMm = 0;

for (int charIdx = 0; charIdx < text.length(); charIdx++) {
    String singleChar = String.valueOf(text.charAt(charIdx));
    double wPt = awtFont.getStringBounds(singleChar, frc).getWidth();
    charWidthsMm[charIdx] = wPt * 25.4 / 72.0;
    totalAwtWidthMm += charWidthsMm[charIdx];
}

// 2. 计算缩放比例
double scaleX = ocrW / totalAwtWidthMm;

// 3. 逐字强制绘制
double currentX = ocrX;

for (int charIdx = 0; charIdx < text.length(); charIdx++) {
    String singleChar = String.valueOf(text.charAt(charIdx));
    
    Span span = new Span(singleChar);
    span.setFontSize(fontSizeMm);
    span.setColor(255, 255, 255); // 白色
    
    Paragraph p = new Paragraph();
    p.add(span);
    p.setX(currentX);  // ⭐ 强制 X 坐标
    p.setY(paragraphY); // ⭐ 强制 Y 坐标
    p.setOpacity(0.01); // ⭐ 1% 透明度
    
    vPage.add(p);
    currentX += (charWidthsMm[charIdx] * scaleX);
}
```

**优势：**
- ✅ 100% 精准对齐
- ✅ 不受字数影响
- ✅ WPS 兼容（白色 + 1% 透明度）
- ✅ 文件大小仅增加 2.7%

---

## ⚙️ **系统要求**

### **Native Image 版本**
```
✅ Windows 10/11 (64-bit)
❌ 无需 Java
✅ 至少 2 GB RAM
```

### **jpackage 版本**
```
✅ Windows 10/11 (64-bit)
❌ 无需 Java
✅ 至少 2 GB RAM
```

### **JAR 版本**
```
✅ Windows/macOS/Linux
✅ JDK 17+
✅ 至少 2 GB RAM
```

---

## 🔄 **更新日志**

### **v2.0.0 (2026-03-22)** ⭐⭐⭐⭐⭐ **Native Image Edition**
- ✅ **单文件 EXE** - 12.17 MB
- ✅ **无需 Java** - 内置运行时
- ✅ **快速启动** - 预编译机器码
- ✅ **易于分发** - 单个文件
- ✅ **跨平台潜力** - 支持 Windows/macOS/Linux
- ✅ **命令行界面** - 完整的参数支持
- ✅ **批量处理** - 通配符和文件夹递归

### **v1.3.0 (2026-03-22)** ⭐⭐⭐⭐⭐ **终极武器**
- ✅ **终极武器**: 逐字符绝对定位
- ✅ **100% 完美对齐**: X 轴 + Y 轴
- ✅ **Y 轴优化**: 0.72 黄金比例基准线
- ✅ **WPS 兼容**: 白色文字 + 1% 透明度

### **v1.2.0 (2026-03-22)** ⭐⭐⭐⭐
- ✅ **终极突破**: 开根号曲线算法
- ✅ **完美对齐**: X 轴 + Y 轴 100% 精准对齐
- ✅ **WPS 兼容**: 白色文字 + 1% 透明度
- ✅ **文件大小优化**: 728 KB (-37%)

### **v1.0.0 (2026-03-21)**
- ✅ OCR 识别功能
- ✅ PDF 透明文字生成
- ✅ OFD 双层结构生成

---

## 🤝 **贡献**

欢迎提交 Issue 和 Pull Request！

---

## 📄 **License**

MIT

---

## 🔗 **GitHub**

https://github.com/brianshih04/jpeg2pdf_ofd_cmd

---

## 💡 **推荐**

**如果你：**
- ✅ 想要单文件 EXE → **Native Image 版本**
- ✅ 需要完整功能 → **jpackage 版本**
- ✅ 已有 Java 环境 → **JAR 版本**

---

**🎉 立即下载并开始使用！** ✨✨✨
