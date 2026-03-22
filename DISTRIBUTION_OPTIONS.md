# 分发方式对比

## 🎯 核心问题

**用户需要安装 Java 环境吗？**

答案：**取决于你提供的分发方式！**

---

## 📊 分发方式对比

### **方式 1：JAR 文件**

```bash
java -jar jpeg2pdf_ofd.jar config.json
```

| 项目 | 评价 |
|------|------|
| **需要 Java** | ❌ **是** (JDK 17+) |
| **文件大小** | ⭐⭐⭐⭐⭐ ~85 MB |
| **跨平台** | ⭐⭐⭐⭐⭐ 完美 |
| **用户体验** | ⭐⭐ 需要安装 Java |
| **开发成本** | ⭐⭐⭐⭐⭐ 最低 |
| **更新方便** | ⭐⭐⭐⭐⭐ 只需替换 JAR |

**适合场景：**
- ✅ 开发者
- ✅ 已有 Java 环境的用户
- ✅ 服务器环境
- ✅ 命令行工具

**不适合：**
- ❌ 普通用户（无 Java 环境）
- ❌ 企业桌面（可能有版本冲突）

---

### **方式 2：jpackage 独立可执行文件** ⭐⭐⭐⭐⭐（推荐）

```bash
# Windows
jpeg2pdf_ofd.exe

# macOS
jpeg2pdf_ofd.app

# Linux
./jpeg2pdf_ofd
```

| 项目 | 评价 |
|------|------|
| **需要 Java** | ✅ **否**（已内置） |
| **文件大小** | ⭐⭐ ~220-250 MB |
| **跨平台** | ⭐⭐⭐ 需要分别打包 |
| **用户体验** | ⭐⭐⭐⭐⭐ 双击即用 |
| **开发成本** | ⭐⭐⭐⭐ 中等 |
| **更新方便** | ⭐⭐⭐ 需要重新下载 |

**适合场景：**
- ✅ 普通用户
- ✅ 企业桌面
- ✅ 无需安装依赖
- ✅ 离线环境

**不适合：**
- ⚠️ 文件体积大
- ⚠️ 需要为每个平台单独打包

---

### **方式 3：便携版（JAR + JRE）**

```
jpeg2pdf_ofd_portable/
├── jpeg2pdf_ofd.jar
├── jre/              (精简的 Java 运行时)
│   ├── bin/
│   └── lib/
└── run.bat           (启动脚本)
```

| 项目 | 评价 |
|------|------|
| **需要 Java** | ✅ **否**（已包含） |
| **文件大小** | ⭐⭐⭐ ~120-150 MB |
| **跨平台** | ⭐⭐⭐ 需要分别打包 |
| **用户体验** | ⭐⭐⭐⭐ 良好 |
| **开发成本** | ⭐⭐⭐⭐ 中等 |
| **更新方便** | ⭐⭐⭐⭐ 只需替换 JAR |

**适合场景：**
- ✅ 便携设备（U盘）
- ✅ 无需安装
- ✅ 可定制启动参数

---

### **方式 4：Docker 容器**

```bash
docker run -v ./data:/data brianshih04/jpeg2pdf_ofd config.json
```

| 项目 | 评价 |
|------|------|
| **需要 Java** | ✅ **否**（容器内） |
| **文件大小** | ⭐⭐ ~300 MB |
| **跨平台** | ⭐⭐⭐⭐⭐ 完美 |
| **用户体验** | ⭐⭐ 需要 Docker 知识 |
| **开发成本** | ⭐⭐⭐⭐ 中等 |
| **更新方便** | ⭐⭐⭐⭐⭐ pull 即可 |

**适合场景：**
- ✅ 服务器环境
- ✅ CI/CD 流程
- ✅ 云端部署
- ✅ 开发者

**不适合：**
- ❌ 普通用户
- ❌ 桌面应用

---

### **方式 5：安装程序（MSI/DMG/DEB）**

```
# Windows
jpeg2pdf_ofd_setup.exe  (安装程序)

# macOS
jpeg2pdf_ofd.dmg

# Linux
jpeg2pdf_ofd.deb
```

| 项目 | 评价 |
|------|------|
| **需要 Java** | ✅ **否**（已内置） |
| **文件大小** | ⭐⭐ ~220-250 MB |
| **跨平台** | ⭐⭐⭐ 需要分别打包 |
| **用户体验** | ⭐⭐⭐⭐⭐ 最佳 |
| **开发成本** | ⭐⭐⭐ 需要签名 |
| **更新方便** | ⭐⭐ 需要重新安装 |

**适合场景：**
- ✅ 企业分发
- ✅ 正式产品
- ✅ 需要安装管理

**不适合：**
- ⚠️ 需要代码签名（$$）
- ⚠️ 复杂的打包流程

---

## 🎯 **推荐方案：混合分发**

### **方案 A：针对开发者** ⭐⭐⭐⭐⭐

```
提供：
1. ✅ JAR 文件（主分发方式）
2. ✅ Docker 镜像
3. ✅ 源代码（GitHub）
```

**适合：**
- 开发者
- 服务器环境
- CI/CD 集成

---

### **方案 B：针对普通用户** ⭐⭐⭐⭐⭐

```
提供：
1. ✅ Windows EXE（jpackage）
2. ✅ macOS DMG
3. ✅ Linux AppImage
4. ✅ 便携版（ZIP）
```

**适合：**
- 普通用户
- 企业桌面
- 离线使用

---

### **方案 C：全面支持** ⭐⭐⭐⭐⭐（最佳）

```
提供：
1. ✅ JAR 文件（85 MB）
   - 适合有 Java 环境的用户
   
2. ✅ Windows EXE（220 MB）
   - 包含 JRE，无需安装 Java
   
3. ✅ 便携版 ZIP（120 MB）
   - 解压即用
   
4. ✅ Docker 镜像
   - 服务器部署
```

**GitHub Release 示例：**
```
v1.0.0
├── jpeg2pdf_ofd-1.0.0.jar           (JAR, 85 MB)
├── jpeg2pdf_ofd-1.0.0-windows.zip    (EXE, 220 MB)
├── jpeg2pdf_ofd-1.0.0-portable.zip   (便携, 120 MB)
├── jpeg2pdf_ofd-1.0.0-macos.dmg      (macOS, 220 MB)
└── jpeg2pdf_ofd-1.0.0-linux.tar.gz   (Linux, 220 MB)
```

---

## 🚀 **实现步骤**

### **Phase 1：JAR 文件（最简单）** ✅ 已完成

```bash
mvn clean package

# 输出
target/jpeg2pdf_ofd-1.0.0.jar  (85 MB)
```

**优点：**
- ✅ 立即可用
- ✅ 跨平台
- ✅ 文件小

**缺点：**
- ❌ 需要 Java 17+

---

### **Phase 2：便携版** ⭐⭐⭐⭐

```bash
# 1. 创建 JRE（使用 jlink）
jlink --output jre \
  --add-modules java.base,java.sql,java.naming \
  --strip-debug \
  --compress 2

# 2. 创建目录结构
jpeg2pdf_ofd_portable/
├── jpeg2pdf_ofd.jar
├── jre/
└── run.bat

# 3. 打包
zip -r jpeg2pdf_ofd-portable.zip jpeg2pdf_ofd_portable
```

**run.bat:**
```batch
@echo off
%~dp0jre\bin\java -jar %~dp0jpeg2pdf_ofd.jar %*
```

**优点：**
- ✅ 无需安装 Java
- ✅ 文件较小（120 MB）
- ✅ 便携

---

### **Phase 3：独立 EXE（jpackage）** ⭐⭐⭐⭐⭐

```bash
# Windows
jpackage \
  --name jpeg2pdf_ofd \
  --input target \
  --main-jar jpeg2pdf_ofd-1.0.0.jar \
  --main-class com.ocr.jpeg2pdf.CliApplication \
  --type app-image \
  --dest dist \
  --java-options "-Xmx2G"

# 打包成 ZIP
Compress-Archive -Path dist\jpeg2pdf_ofd -DestinationPath jpeg2pdf_ofd-windows.zip
```

**优点：**
- ✅ 无需安装 Java
- ✅ 双击运行
- ✅ 最佳用户体验

**缺点：**
- ⚠️ 文件大（220 MB）
- ⚠️ 需要为每个平台单独打包

---

### **Phase 4：安装程序**

```bash
# Windows (需要 WiX)
jpackage \
  --name jpeg2pdf_ofd \
  --input target \
  --main-jar jpeg2pdf_ofd-1.0.0.jar \
  --type msi \
  --win-menu \
  --win-shortcut

# macOS
jpackage \
  --name jpeg2pdf_ofd \
  --input target \
  --main-jar jpeg2pdf_ofd-1.0.0.jar \
  --type dmg \
  --mac-package-name "JPEG2PDF-OFD"
```

---

## 📊 **文件大小对比**

| 方式 | 大小 | 包含 JRE |
|------|------|---------|
| **JAR** | 85 MB | ❌ 否 |
| **便携版** | 120 MB | ✅ 是（精简） |
| **jpackage EXE** | 220 MB | ✅ 是（完整） |
| **安装程序** | 220 MB | ✅ 是（完整） |
| **Docker** | 300 MB | ✅ 是（基础镜像） |

---

## 🎯 **最终推荐**

### **立即提供：**

1. ✅ **JAR 文件**（85 MB）
   - 主要分发方式
   - README 中说明需要 Java 17+

2. ✅ **便携版 ZIP**（120 MB）
   - 无需安装 Java
   - 解压即用
   - 适合 Windows 用户

---

### **后续提供：**

3. ⏳ **Windows EXE**（220 MB）
   - 最佳用户体验
   - 双击运行

4. ⏳ **Docker 镜像**
   - 服务器部署
   - CI/CD 集成

---

## 📝 **README 说明**

```markdown
## 系统要求

### 方式 1：JAR 文件（推荐开发者）
- **需要**：Java 17 或更高版本
- **下载**：jpeg2pdf_ofd-1.0.0.jar (85 MB)

### 方式 2：便携版（推荐普通用户）
- **无需**：安装 Java
- **下载**：jpeg2pdf_ofd-portable.zip (120 MB)
- **使用**：解压后运行 `run.bat` (Windows) 或 `run.sh` (Linux/macOS)

### 方式 3：独立 EXE（推荐 Windows 用户）
- **无需**：安装 Java
- **下载**：jpeg2pdf_ofd-windows.zip (220 MB)
- **使用**：双击 `jpeg2pdf_ofd.exe`

## 安装 Java（如果需要）

### Windows
1. 下载：https://adoptium.net/
2. 选择：JDK 17 (LTS)
3. 安装后重启命令行

### macOS
```bash
brew install openjdk@17
```

### Linux
```bash
sudo apt install openjdk-17-jdk  # Ubuntu/Debian
sudo yum install java-17-openjdk # CentOS/RHEL
```
```

---

## 💬 **总结**

### **问题：用户需要安装 Java 吗？**

**答案：**
- 如果提供 **JAR 文件** → **需要安装 Java** ❌
- 如果提供 **便携版/EXE** → **不需要安装 Java** ✅

### **推荐策略：**

```
1. ✅ 立即提供：JAR + 便携版
2. ⏳ 后续提供：独立 EXE + Docker
3. 📝 清晰说明：每种方式的系统要求
```

### **用户体验优先级：**

1. **便携版** - 解压即用，无需安装（推荐）
2. **独立 EXE** - 双击运行，最佳体验
3. **JAR 文件** - 适合开发者
4. **Docker** - 适合服务器

---

**你觉得这个方案怎么样？我们应该先实现哪一种？** 🎯✨
