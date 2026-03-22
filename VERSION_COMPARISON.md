# 三種版本對比

## 📊 **版本對比表**

| 特性 | Native Image | jpackage | Spring Boot JAR |
|------|-------------|----------|-----------------|
| **文件大小** | **12.17 MB** | 212 MB | 83.32 MB |
| **需要 Java** | ❌ 否 | ❌ 否 | ✅ 是 |
| **單文件** | ✅ 是 | ❌ 資料夾 | ✅ 是 |
| **OCR 功能** | ⚠️ 占位符 | ✅ 完整 | ✅ 完整 |
| **PDF/OFD 生成** | ⚠️ 占位符 | ✅ 完整 | ✅ 完整 |
| **語言支持** | ✅ 80+ | ✅ 80+ | ✅ 80+ |
| **啟動速度** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **易用性** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **功能完整度** | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **生產就緒** | ❌ 基礎版 | ✅ 完整 | ✅ 完整 |
| **推薦度** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

---

## 🚀 **快速選擇**

### **如果你需要...**

#### **單文件 EXE，現在就要用** → ⚠️ 不推薦
```
Native Image 版本只有框架，沒有實際 OCR 功能

選擇：jpackage 版本
```

#### **完整功能，無需 Java** → ✅ 推薦
```
jpackage 版本
- 完整 OCR 功能
- 無需安裝 Java
- 立即可用

位置：dist-exe\JPEG2PDF-OFD\
```

#### **已有 Java 環境** → ✅ 可用
```
Spring Boot JAR
- 完整功能
- 單文件
- 需要 Java 17+

位置：target\jpeg2pdf-ofd-1.0.0.jar
```

---

## 📂 **文件位置**

### **1. jpackage 版本（推薦）** ⭐⭐⭐⭐⭐

```
D:\Projects\jpeg2pdf_ofd_cmd\dist-exe\JPEG2PDF-OFD\

文件：
├── JPEG2PDF-OFD.exe          (主程式，433 KB)
├── app\                       (應用程式)
├── runtime\                   (JRE)
├── config-simple.json         (簡單配置)
└── 使用說明.bat              (使用說明)

大小：212 MB（整個資料夾）

使用：
  cd dist-exe\JPEG2PDF-OFD
  JPEG2PDF-OFD.exe config-simple.json
```

### **2. Native Image 版本（基礎版）** ⭐⭐⭐

```
D:\Projects\jpeg2pdf_ofd_cmd\jpeg2pdf-ofd.exe

大小：12.17 MB（單文件）

狀態：
  ✅ 命令行框架
  ✅ 文件處理
  ❌ OCR 功能（占位符）
  ❌ PDF/OFD 生成（占位符）

使用：
  jpeg2pdf-ofd.exe image.jpg output/

適用場景：
  - 自動化腳本框架
  - 未來集成 OCR
  - 學習 Native Image
```

### **3. Spring Boot JAR 版本** ⭐⭐⭐⭐

```
D:\Projects\jpeg2pdf_ofd_cmd\target\jpeg2pdf-ofd-1.0.0.jar

大小：83.32 MB（單文件）

需要：Java 17+

使用：
  java -jar jpeg2pdf-ofd-1.0.0.jar

特點：
  ✅ 完整功能
  ✅ Web UI (http://localhost:8000)
  ✅ REST API
```

---

## 🎯 **使用場景**

### **場景 1：一般用戶**

```
推薦：jpackage 版本

理由：
  ✅ 無需安裝 Java
  ✅ 完整功能
  ✅ 易於使用
  ✅ 雙擊即可運行

步驟：
  1. 複製整個 dist-exe\JPEG2PDF-OFD 資料夾
  2. 將圖片放到 C:\OCR\Watch
  3. 雙擊 JPEG2PDF-OFD.exe
```

### **場景 2：開發者**

```
推薦：Spring Boot JAR

理由：
  ✅ 已有 Java 環境
  ✅ Web UI 方便調試
  ✅ REST API 可集成

步驟：
  1. java -jar target\jpeg2pdf-ofd-1.0.0.jar
  2. 打開 http://localhost:8000
```

### **場景 3：自動化腳本**

```
推薦：jpackage 版本（目前）

理由：
  ✅ 完整功能
  ✅ 命令行支持
  ✅ JSON 配置

步驟：
  1. 創建 config.json
  2. 執行 JPEG2PDF-OFD.exe config.json
  3. 自動處理所有圖片
```

---

## ⚙️ **配置示例**

### **簡單配置（config-simple.json）**

```json
{
  "input": "C:/OCR/Watch",
  "output": {
    "folder": "C:/OCR/Output",
    "format": "pdf"
  },
  "ocr": {
    "language": "chinese_cht"
  }
}
```

### **批量配置（config-batch.json）**

```json
{
  "input": {
    "folder": "C:/OCR/Watch",
    "pattern": "*.jpg"
  },
  "output": {
    "folder": "C:/OCR/Output",
    "format": "all"
  },
  "ocr": {
    "language": "chinese_cht"
  }
}
```

### **高級配置（config-advanced.json）**

```json
{
  "input": {
    "folder": "C:/OCR/Watch",
    "pattern": "*.jpg",
    "recursive": true
  },
  "output": {
    "folder": "C:/OCR/Output",
    "format": "ofd",
    "filename_template": "{original}_{timestamp}"
  },
  "ocr": {
    "language": "chinese_cht",
    "det_db_thresh": 0.3,
    "det_db_box_thresh": 0.5
  },
  "watch": {
    "enabled": true,
    "interval_ms": 5000
  }
}
```

---

## 🔄 **後續計劃**

### **Native Image 版本增強**

**需要做的**：
1. ✅ 命令行框架（已完成）
2. ⏳ 集成 RapidOCR（需要 2-4 小時）
3. ⏳ 集成 PDFBox（需要 1-2 小時）
4. ⏳ 集成 ofdrw（需要 1-2 小時）
5. ⏳ 測試所有功能

**挑戰**：
- RapidOCR 的 JNI 調用
- Native Image 反射配置
- OCR 模型文件路徑
- 依賴大小增加

**預計完成時間**：4-8 小時

**預計最終大小**：50-80 MB（包含 OCR 模型）

---

## 💡 **建議**

### **現在就用 jpackage 版本！** ⭐⭐⭐⭐⭐

**為什麼？**

1. ✅ **立即可用** - 無需等待
2. ✅ **完整功能** - OCR + PDF/OFD 生成
3. ✅ **無需 Java** - 內置 JRE
4. ✅ **穩定可靠** - 已經測試過
5. ⚠️ **只是資料夾** - 不影響使用

**Native Image 版本**可以作為**未來優化**的目標：
- 當需要真正的單文件時再處理
- 需要更多時間集成 OCR
- 可以作為學習 Native Image 的案例

---

## 📞 **技術支持**

**問題排查**：

1. **jpackage 版本無法啟動**
   ```
   檢查：
   - 是否有整個資料夾（app/, runtime/）
   - 防火牆是否阻止
   - 是否有寫入權限
   ```

2. **JAR 版本無法啟動**
   ```
   檢查：
   - Java 版本是否 17+
   - 埠 8000 是否被占用
   - 是否有足夠內存
   ```

3. **OCR 識別不準確**
   ```
   嘗試：
   - 更換語言設置
   - 調整 OCR 參數
   - 使用更高質量的圖片
   ```

---

## 🎉 **總結**

| 場景 | 推薦版本 | 原因 |
|------|---------|------|
| 一般用戶 | jpackage | 完整功能，易於使用 |
| 開發者 | JAR | Web UI，方便調試 |
| 自動化 | jpackage | 完整功能，命令行支持 |
| 未來優化 | Native Image | 單文件，需要開發 |

**現在就用 jpackage 版本！** ✨

---

**文檔版本**：v1.0
**更新時間**：2026-03-22 23:50
**作者**：Brian Shih
