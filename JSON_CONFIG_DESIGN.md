# JSON 配置文件方案设计

## 💡 核心思想

**命令行极简化：**
```bash
java -jar jpeg2pdf-ofd.jar config.json
```

**所有配置都在 JSON 文件中！**

---

## 📝 JSON 配置文件格式

### **基础示例**

```json
{
  "input": "scan.jpg",
  "output": "output.pdf",
  "format": "pdf",
  "language": "chinese_cht"
}
```

---

### **完整配置示例**

```json
{
  "version": "1.0",
  
  "input": {
    "file": "scan.jpg",
    "folder": null,
    "pattern": "*.jpg",
    "recursive": false
  },
  
  "output": {
    "file": "output.pdf",
    "folder": "./output",
    "format": "pdf",
    "filenameTemplate": "{original}_{timestamp}"
  },
  
  "ocr": {
    "enabled": true,
    "language": "chinese_cht",
    "useAngleCls": true,
    "useGpu": false
  },
  
  "pdf": {
    "invisibleText": true,
    "textOpacity": 0.01,
    "textColor": "white"
  },
  
  "ofd": {
    "invisibleText": true,
    "textOpacity": 0.01,
    "textColor": "white"
  },
  
  "logging": {
    "level": "info",
    "file": "ocr.log",
    "quiet": false,
    "verbose": false
  }
}
```

---

## 🎯 使用场景

### **场景 1：转换单个文件**

**config.json:**
```json
{
  "input": {
    "file": "document.jpg"
  },
  "output": {
    "file": "document.pdf",
    "format": "pdf"
  },
  "ocr": {
    "language": "chinese_cht"
  }
}
```

**命令行：**
```bash
java -jar jpeg2pdf-ofd.jar config.json
```

---

### **场景 2：批量转换**

**batch-config.json:**
```json
{
  "input": {
    "folder": "./images",
    "pattern": "*.jpg",
    "recursive": true
  },
  "output": {
    "folder": "./output",
    "format": "pdf",
    "filenameTemplate": "{original}_{timestamp}"
  },
  "ocr": {
    "language": "chinese_cht"
  }
}
```

**命令行：**
```bash
java -jar jpeg2pdf-ofd.jar batch-config.json
```

---

### **场景 3：多格式输出**

**multi-format.json:**
```json
{
  "input": {
    "file": "invoice.jpg"
  },
  "output": {
    "folder": "./output",
    "format": ["pdf", "ofd", "txt"],
    "filenameTemplate": "{original}_{timestamp}"
  },
  "ocr": {
    "language": "chinese_cht"
  }
}
```

**命令行：**
```bash
java -jar jpeg2pdf-ofd.jar multi-format.json
```

---

### **场景 4：多文件任务列表**

**tasks.json:**
```json
{
  "tasks": [
    {
      "input": "doc1.jpg",
      "output": "doc1.pdf",
      "language": "chinese_cht"
    },
    {
      "input": "doc2.jpg",
      "output": "doc2.pdf",
      "language": "en"
    },
    {
      "input": "doc3.jpg",
      "output": "doc3.pdf",
      "language": "japan"
    }
  ]
}
```

**命令行：**
```bash
java -jar jpeg2pdf-ofd.jar tasks.json
```

---

## 🔧 配置文件详解

### **顶层结构**

```json
{
  "version": "1.0",           // 配置文件版本（可选）
  "input": { ... },           // 输入配置
  "output": { ... },          // 输出配置
  "ocr": { ... },             // OCR 配置
  "pdf": { ... },             // PDF 特定配置（可选）
  "ofd": { ... },             // OFD 特定配置（可选）
  "logging": { ... },         // 日志配置（可选）
  "tasks": [ ... ]            // 任务列表（批量任务，可选）
}
```

---

### **input 配置**

```json
"input": {
  "file": "image.jpg",         // 单个文件
  "folder": "./images",        // 文件夹（批量处理）
  "pattern": "*.jpg",          // 文件匹配模式（默认：*.jpg）
  "recursive": true,           // 递归处理子文件夹（默认：false）
  
  // 二选一
  "files": [                   // 明确的文件列表
    "file1.jpg",
    "file2.jpg",
    "file3.jpg"
  ]
}
```

---

### **output 配置**

```json
"output": {
  "file": "output.pdf",        // 输出文件名（单文件）
  "folder": "./output",        // 输出文件夹（批量处理）
  "format": "pdf",             // 输出格式
  // format 选项：
  // - "pdf"    : 可搜索 PDF
  // - "ofd"    : 可搜索 OFD
  // - "txt"    : 纯文本
  // - "all"    : 所有格式
  // - ["pdf", "ofd", "txt"] : 指定多个格式
  
  "filenameTemplate": "{original}_{timestamp}",
  // 文件名模板变量：
  // - {original}  : 原文件名（不含扩展名）
  // - {timestamp} : 时间戳（yyyyMMdd_HHmmss）
  // - {date}      : 日期（yyyyMMdd）
  // - {time}      : 时间（HHmmss）
  // - {language}  : OCR 语言
  // - {format}    : 输出格式
  
  "overwrite": false           // 是否覆盖已存在的文件（默认：false）
}
```

---

### **ocr 配置**

```json
"ocr": {
  "enabled": true,             // 是否启用 OCR（默认：true）
  "language": "chinese_cht",   // OCR 语言
  // 支持的语言：
  // - chinese_cht (繁体中文)
  // - ch (简体中文)
  // - en (英文)
  // - japan (日文)
  // - korean (韩文)
  // - french, german, spanish, etc. (80+ 种语言)
  
  "useAngleCls": true,         // 启用角度分类（默认：true）
  "useGpu": false,             // 使用 GPU 加速（默认：false）
  "detDbThresh": 0.3,          // 检测阈值（高级，可选）
  "detDbBoxThresh": 0.5,       // 检测框阈值（高级，可选）
  
  "minConfidence": 0.5         // 最小置信度（过滤低质量识别，可选）
}
```

---

### **pdf 配置（可选）**

```json
"pdf": {
  "invisibleText": true,       // 不可见文字层（默认：true）
  "textOpacity": 0.01,         // 文字透明度（默认：0.01）
  "textColor": "white",        // 文字颜色（默认：white）
  // 颜色格式：
  // - "white", "black", "red", etc.
  // - "#FFFFFF", "#000000", etc.
  // - "rgb(255,255,255)", etc.
  
  "fontSize": 12,              // 字体大小（可选）
  "fontFamily": "Arial",       // 字体（可选）
  "pageSize": "A4",            // 页面大小（可选）
  // 页面大小选项：
  // - "A4", "A3", "Letter", etc.
  // - { "width": 210, "height": 297, "unit": "mm" }
  
  "metadata": {                // PDF 元数据（可选）
    "title": "Scanned Document",
    "author": "JPEG2PDF-OFD",
    "subject": "OCR Document",
    "creator": "JPEG2PDF-OFD v1.0.0"
  }
}
```

---

### **ofd 配置（可选）**

```json
"ofd": {
  "invisibleText": true,       // 不可见文字层（默认：true）
  "textOpacity": 0.01,         // 文字透明度（默认：0.01）
  "textColor": "white",        // 文字颜色（默认：white）
  "pageSize": "A4",            // 页面大小（可选）
  
  "metadata": {                // OFD 元数据（可选）
    "title": "Scanned Document",
    "author": "JPEG2PDF-OFD"
  }
}
```

---

### **logging 配置（可选）**

```json
"logging": {
  "level": "info",             // 日志级别（默认：info）
  // 日志级别：
  // - "error"  : 只显示错误
  // - "warn"   : 显示警告和错误
  // - "info"   : 显示信息、警告和错误
  // - "debug"  : 显示调试信息
  // - "trace"  : 显示所有信息
  
  "file": "ocr.log",           // 日志文件（可选）
  "quiet": false,              // 静默模式（默认：false）
  "verbose": false,            // 详细模式（默认：false）
  
  "progress": true,            // 显示进度条（默认：true）
  "colors": true               // 彩色输出（默认：true）
}
```

---

### **tasks 配置（批量任务）**

```json
"tasks": [
  {
    "input": "doc1.jpg",
    "output": "doc1.pdf",
    "language": "chinese_cht"
  },
  {
    "input": "doc2.jpg",
    "output": "doc2.pdf",
    "language": "en"
  }
]
```

---

## 🎯 命令行选项

### **基本用法**

```bash
# 使用配置文件
java -jar jpeg2pdf-ofd.jar config.json

# 使用简写
java -jar jpeg2pdf-ofd.jar -c config.json

# 显示帮助
java -jar jpeg2pdf-ofd.jar --help
java -jar jpeg2pdf-ofd.jar -h

# 显示版本
java -jar jpeg2pdf-ofd.jar --version
java -jar jpeg2pdf-ofd.jar -v
```

---

### **验证配置文件**

```bash
# 验证配置文件（不执行转换）
java -jar jpeg2pdf-ofd.jar config.json --validate

# 生成示例配置文件
java -jar jpeg2pdf-ofd.jar --generate-sample > sample.json

# 生成完整配置文件
java -jar jpeg2pdf-ofd.jar --generate-full > full-config.json
```

---

## 📝 配置文件模板

### **模板 1：简单模板**

```json
{
  "input": "image.jpg",
  "output": "output.pdf",
  "language": "chinese_cht"
}
```

---

### **模板 2：批量处理模板**

```json
{
  "input": {
    "folder": "./images",
    "pattern": "*.jpg"
  },
  "output": {
    "folder": "./output",
    "format": "pdf"
  },
  "ocr": {
    "language": "chinese_cht"
  }
}
```

---

### **模板 3：高级模板**

```json
{
  "version": "1.0",
  
  "input": {
    "folder": "./images",
    "pattern": "*.jpg",
    "recursive": true
  },
  
  "output": {
    "folder": "./output",
    "format": ["pdf", "ofd", "txt"],
    "filenameTemplate": "{original}_{timestamp}",
    "overwrite": false
  },
  
  "ocr": {
    "language": "chinese_cht",
    "useAngleCls": true,
    "minConfidence": 0.7
  },
  
  "pdf": {
    "invisibleText": true,
    "textOpacity": 0.01,
    "textColor": "white"
  },
  
  "ofd": {
    "invisibleText": true,
    "textOpacity": 0.01,
    "textColor": "white"
  },
  
  "logging": {
    "level": "info",
    "file": "ocr.log",
    "progress": true
  }
}
```

---

## 🎨 配置文件生成器

### **命令行生成**

```bash
# 生成简单配置
java -jar jpeg2pdf-ofd.jar --generate simple > config.json

# 生成批量配置
java -jar jpeg2pdf-ofd.jar --generate batch > batch-config.json

# 生成高级配置
java -jar jpeg2pdf-ofd.jar --generate advanced > advanced-config.json

# 生成完整配置（包含所有选项）
java -jar jpeg2pdf-ofd.jar --generate full > full-config.json
```

---

## 💾 配置文件继承

### **base-config.json**
```json
{
  "ocr": {
    "language": "chinese_cht",
    "useAngleCls": true
  },
  "output": {
    "folder": "./output",
    "format": "pdf"
  }
}
```

### **specific-config.json**
```json
{
  "extends": "base-config.json",
  "input": {
    "file": "special-document.jpg"
  },
  "ocr": {
    "language": "en"
  }
}
```

**说明：**
- `specific-config.json` 继承 `base-config.json` 的所有配置
- 相同字段会被覆盖

---

## 🚀 实现优先级

### **Phase 1: 基本功能**
```
1. ✅ JSON 配置文件解析
2. ✅ 单文件转换
3. ✅ 基本输出格式（PDF）
4. ✅ OCR 语言选择
```

### **Phase 2: 批量处理**
```
1. ⏳ 文件夹批量处理
2. ⏳ 文件模式匹配
3. ⏳ 递归处理
4. ⏳ 多格式输出
```

### **Phase 3: 高级功能**
```
1. ⏳ 配置文件继承
2. ⏳ 配置文件验证
3. ⏳ 配置文件生成器
4. ⏳ 任务列表
```

---

## 📋 对比

| 特性 | 命令行参数 | JSON 配置 |
|------|-----------|-----------|
| **简单任务** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **复杂任务** | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| **可读性** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **可维护性** | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| **可重用性** | ⭐ | ⭐⭐⭐⭐⭐ |
| **版本控制** | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| **学习曲线** | ⭐⭐⭐⭐ | ⭐⭐⭐ |

---

## 💬 总结

**JSON 配置文件方案的优势：**

1. ✅ **简洁的命令行** - 只需要一个参数
2. ✅ **配置可重用** - 保存后可以重复使用
3. ✅ **易于维护** - JSON 格式清晰易读
4. ✅ **版本控制友好** - 可以提交到 Git
5. ✅ **支持复杂场景** - 批量任务、多格式等
6. ✅ **易于生成** - 可以通过工具生成配置

**命令行仍然支持：**
```bash
java -jar jpeg2pdf-ofd.jar --help
java -jar jpeg2pdf-ofd.jar --version
java -jar jpeg2pdf-ofd.jar --validate config.json
java -jar jpeg2pdf-ofd.jar --generate sample > config.json
```

---

**你觉得这个设计怎么样？需要调整吗？** 🎯✨
