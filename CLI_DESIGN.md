# 命令行版本设计

## 📋 命令行格式讨论

---

## 🎯 设计原则

1. **简单易用** - 参数清晰，符合常见 CLI 规范
2. **功能完整** - 支持所有核心功能
3. **批处理友好** - 支持批量操作
4. **跨平台** - Windows/Linux/macOS 通用
5. **错误提示清晰** - 友好的错误消息

---

## 💡 命令行格式方案

### **方案 A：位置参数** ⭐⭐⭐⭐

```bash
java -jar jpeg2pdf-ofd.jar <input> [output] [options]

# 示例
java -jar jpeg2pdf-ofd.jar image.jpg
java -jar jpeg2pdf-ofd.jar image.jpg output.pdf
java -jar jpeg2pdf-ofd.jar image.jpg output.ofd --lang chinese_cht
java -jar jpeg2pdf-ofd.jar *.jpg output.pdf --batch
```

**优点：**
- ✅ 简单直观
- ✅ 符合传统 CLI 习惯
- ✅ 适合快速操作

**缺点：**
- ⚠️ 参数顺序固定
- ⚠️ 可选参数需要放在最后

---

### **方案 B：命名参数** ⭐⭐⭐⭐⭐（推荐）

```bash
java -jar jpeg2pdf-ofd.jar --input <file> --output <file> [options]

# 简写
java -jar jpeg2pdf-ofd.jar -i image.jpg -o output.pdf

# 完整参数
java -jar jpeg2pdf-ofd.jar \
  --input image.jpg \
  --output output.pdf \
  --format pdf \
  --lang chinese_cht \
  --ocr

# 批量处理
java -jar jpeg2pdf-ofd.jar \
  --input "*.jpg" \
  --output-folder ./output \
  --format pdf \
  --batch
```

**优点：**
- ✅ 参数顺序灵活
- ✅ 可读性强
- ✅ 易于扩展
- ✅ 支持简写

**缺点：**
- ⚠️ 输入稍微长一点

---

### **方案 C：子命令模式** ⭐⭐⭐

```bash
java -jar jpeg2pdf-ofd.jar <command> [options]

# 转换单个文件
java -jar jpeg2pdf-ofd.jar convert --input image.jpg --output output.pdf

# 批量转换
java -jar jpeg2pdf-ofd.jar batch --input-folder ./images --output-folder ./output

# Watch 模式
java -jar jpeg2pdf-ofd.jar watch --folder ./watch --output-folder ./output
```

**优点：**
- ✅ 功能分组清晰
- ✅ 适合复杂场景

**缺点：**
- ⚠️ 输入较长
- ⚠️ 对于简单操作过于复杂

---

## 🎯 **推荐方案：方案 B（命名参数）**

---

## 📝 **完整命令行参数设计**

### **基本用法**

```bash
# 最简单用法
java -jar jpeg2pdf-ofd.jar -i image.jpg

# 指定输出
java -jar jpeg2pdf-ofd.jar -i image.jpg -o output.pdf

# 指定格式和语言
java -jar jpeg2pdf-ofd.jar -i image.jpg -o output.pdf -f ofd -l chinese_cht
```

---

### **参数列表**

#### **必需参数**

| 参数 | 简写 | 说明 | 示例 |
|------|------|------|------|
| `--input` | `-i` | 输入文件或模式 | `-i image.jpg` |
| | | | `-i "*.jpg"` |
| | | | `-i ./images/` |

#### **输出参数**

| 参数 | 简写 | 说明 | 默认值 |
|------|------|------|--------|
| `--output` | `-o` | 输出文件名 | `<input>.pdf` |
| `--output-folder` | `-of` | 输出文件夹 | 当前目录 |
| `--format` | `-f` | 输出格式 | `pdf` |
| | | | `pdf`, `ofd`, `txt`, `all` |

#### **OCR 参数**

| 参数 | 简写 | 说明 | 默认值 |
|------|------|------|--------|
| `--lang` | `-l` | OCR 语言 | `chinese_cht` |
| `--ocr` | | 启用 OCR | `true` |
| `--no-ocr` | | 禁用 OCR | - |

#### **批量处理参数**

| 参数 | 简写 | 说明 | 默认值 |
|------|------|------|--------|
| `--batch` | `-b` | 批量处理模式 | - |
| `--recursive` | `-r` | 递归处理子文件夹 | `false` |
| `--pattern` | `-p` | 文件匹配模式 | `*.jpg` |

#### **其他参数**

| 参数 | 简写 | 说明 | 默认值 |
|------|------|------|--------|
| `--help` | `-h` | 显示帮助 | - |
| `--version` | `-v` | 显示版本 | - |
| `--quiet` | `-q` | 静默模式 | `false` |
| `--verbose` | | 详细输出 | `false` |
| `--config` | `-c` | 配置文件 | - |

---

## 🎯 **使用场景示例**

### **场景 1：转换单个图片**

```bash
# PDF（默认）
java -jar jpeg2pdf-ofd.jar -i scan.jpg

# OFD
java -jar jpeg2pdf-ofd.jar -i scan.jpg -f ofd

# 所有格式
java -jar jpeg2pdf-ofd.jar -i scan.jpg -f all
```

---

### **场景 2：批量转换**

```bash
# 批量转换所有 JPG
java -jar jpeg2pdf-ofd.jar -i "*.jpg" -of ./output -b

# 批量转换，指定格式
java -jar jpeg2pdf-ofd.jar -i "*.jpg" -of ./output -f pdf -b

# 递归处理子文件夹
java -jar jpeg2pdf-ofd.jar -i ./images -of ./output -r -b
```

---

### **场景 3：多语言 OCR**

```bash
# 繁体中文（默认）
java -jar jpeg2pdf-ofd.jar -i image.jpg -l chinese_cht

# 英文
java -jar jpeg2pdf-ofd.jar -i image.jpg -l en

# 日文
java -jar jpeg2pdf-ofd.jar -i image.jpg -l japan

# 多语言混合
java -jar jpeg2pdf-ofd.jar -i image.jpg -l "chinese_cht,japan,en"
```

---

### **场景 4：Watch 模式（持续监控）**

```bash
# 启动 Watch 模式
java -jar jpeg2pdf-ofd.jar --watch --folder ./watch --output-folder ./output

# 后台运行（Linux/macOS）
nohup java -jar jpeg2pdf-ofd.jar --watch --folder ./watch --output-folder ./output &

# Windows 服务
sc create JPEG2PDF binPath= "java -jar jpeg2pdf-ofd.jar --watch --folder C:\OCR\Watch --output-folder C:\OCR\Output"
```

---

### **场景 5：高级配置**

```bash
# 使用配置文件
java -jar jpeg2pdf-ofd.jar --config settings.conf -i image.jpg

# 详细日志
java -jar jpeg2pdf-ofd.jar -i image.jpg --verbose

# 静默模式
java -jar jpeg2pdf-ofd.jar -i image.jpg --quiet
```

---

## 🔧 **配置文件示例（settings.conf）**

```properties
# 默认 OCR 语言
ocr.language=chinese_cht

# 默认输出格式
output.format=pdf

# 默认输出文件夹
output.folder=./output

# OCR 选项
ocr.enabled=true
ocr.useAngleCls=true

# 文件命名格式
output.filename={original}_{timestamp}
```

---

## 📊 **输出信息**

### **正常输出**

```
[INFO] JPEG2PDF-OFD v1.0.0
[INFO] 输入: image.jpg (1024x768, 150 KB)
[INFO] OCR 语言: chinese_cht
[INFO] OCR 进度: ████████████████████ 100% (3.2s)
[INFO] 导出格式: PDF
[INFO] 输出: output.pdf (245 KB)
[INFO] 完成！耗时: 3.5 秒
```

---

### **静默模式（--quiet）**

```
output.pdf
```

---

### **详细模式（--verbose）**

```
[DEBUG] 加载图片: image.jpg
[DEBUG] 图片尺寸: 1024x768
[DEBUG] 启动 OCR 引擎
[DEBUG] OCR 模型: chinese_cht
[DEBUG] 识别文字行数: 25
[DEBUG] 识别字符数: 350
[DEBUG] 平均置信度: 0.95
[DEBUG] 创建 PDF 文档
[DEBUG] 添加不可见文字层
[DEBUG] 保存文件: output.pdf
[INFO] 完成！
```

---

## 🎯 **错误处理**

### **错误示例**

```bash
# 文件不存在
[ERROR] 找不到输入文件: notexist.jpg
用法: java -jar jpeg2pdf-ofd.jar -i <input> [-o <output>]

# 不支持的格式
[ERROR] 不支持的输出格式: docx
支持的格式: pdf, ofd, txt, all

# OCR 失败
[ERROR] OCR 识别失败: 无法识别图片中的文字
提示: 请检查图片质量和 OCR 语言设置
```

---

## 🚀 **实现优先级**

### **Phase 1: 核心功能**
```
1. ✅ 基本命令行解析
2. ✅ 单文件转换（PDF）
3. ✅ OCR 集成
4. ✅ 语言选择
```

### **Phase 2: 批量处理**
```
1. ⏳ 批量转换模式
2. ⏳ 文件模式匹配
3. ⏳ 递归处理
```

### **Phase 3: 高级功能**
```
1. ⏳ Watch 模式
2. ⏳ 配置文件支持
3. ⏳ 详细日志
4. ⏳ 进度显示
```

---

## 💬 **讨论**

**你觉得这个设计怎么样？**

**特别关注：**
1. 参数格式是否清晰？
2. 命名是否合理？
3. 是否需要调整？
4. 有没有遗漏的场景？

**下一步：**
1. 确定最终的命令行格式
2. 实现命令行解析器
3. 简化依赖（移除 Web 相关）
4. 测试基本功能
