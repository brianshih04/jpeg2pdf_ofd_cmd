# 研究報告：如何移除 Spring Boot

---

## 📋 **當前 Spring Boot 依賴分析**

### **1. 主程序**

```
CliApplication.java
├── @SpringBootApplication
├── @Autowired Service
└── SpringApplication.run()
```

### **2. Service 層**

```
RapidOcrServiceImpl.java    @Service, @Autowired AppConfig
PdfBoxServiceImpl.java      @Service, @Autowired AppConfig
TextServiceImpl.java        @Service
OfdLayoutDirectServiceImpl.java  @Service
```

### **3. 配置層**

```
AppConfig.java
├── @Configuration
├── @ConfigurationProperties(prefix = "app")
└── application.yml
```

---

## 🔧 **移除策略**

### **方案 1：保留接口，移除註解** ⭐⭐⭐⭐⭐

```
保留：
✅ Service 接口
✅ Service 實現類（邏輯）

移除：
❌ @SpringBootApplication
❌ @Service
❌ @Autowired
❌ @Configuration
❌ Spring Boot 依賴

替換：
🔄 依賴注入 → 手動創建實例
🔄 配置讀取 → 直接讀取 Properties/JSON
🔄 主程序 → 純 Java main
```

---

## 💡 **實現步驟**

### **步驟 1：簡化配置類**

```java
// AppConfig.java（無 Spring Boot）
public class AppConfig {
    private String outputFolder = "C:/OCR/Output";
    private String watchFolder = "C:/OCR/Watch";
    private String fontPath = getDefaultFontPath();
    
    // Getters & Setters
    // 靜態工廠方法
    public static AppConfig loadFromFile(String path) {
        // 讀取 JSON 或 Properties
    }
}
```

### **步驟 2：修改 Service 實現**

```java
// RapidOcrServiceImpl.java（無 @Service）
public class RapidOcrServiceImpl implements OcrService {
    private final AppConfig config;
    
    // 構造函數注入（不是 @Autowired）
    public RapidOcrServiceImpl(AppConfig config) {
        this.config = config;
    }
    
    // 方法實現保持不變
}
```

### **步驟 3：創建純 Java 主程序**

```java
// NoSpringCli.java
public class NoSpringCli {
    public static void main(String[] args) {
        // 1. 創建配置
        AppConfig config = AppConfig.loadFromFile("config.json");
        
        // 2. 手動創建 Service
        OcrService ocrService = new RapidOcrServiceImpl(config);
        PdfService pdfService = new PdfBoxServiceImpl(config);
        TextService textService = new TextServiceImpl();
        
        // 3. 使用 Service
        List<OcrResult> results = ocrService.batchRecognize(images, language);
        pdfService.generateSearchablePdf(images, results, outputPath);
    }
}
```

---

## 📊 **對比**

| 項目 | Spring Boot 版本 | 純 Java SE 版本 |
|------|------------------|----------------|
| **JAR 大小** | 83 MB | 預計 20-30 MB |
| **啟動時間** | ~1.5 秒 | <1 秒 |
| **內存使用** | ~150 MB | ~50-80 MB |
| **複雜度** | 低（自動配置） | 中（手動管理） |
| **依賴** | Spring Boot | 無框架 |

---

## 🎯 **預期收益**

### **移除 Spring Boot 後**

```
✅ JAR 大小減少：83 MB → 20-30 MB (-60-70%)
✅ 啟動時間：1.5 秒 → <1 秒 (-30-50%)
✅ 內存使用：150 MB → 50-80 MB (-50-70%)
✅ 無框架依賴
✅ 更簡單的架構
```

---

## ⚠️ **需要處理的問題**

### **1. 配置管理**

```java
// 當前：application.yml + @ConfigurationProperties
// 替換：config.json + 手動讀取

Properties props = new Properties();
props.load(new FileInputStream("config.properties"));

// 或使用 Jackson 讀取 JSON
ObjectMapper mapper = new ObjectMapper();
AppConfig config = mapper.readValue(new File("config.json"), AppConfig.class);
```

### **2. 依賴注入**

```java
// 當前：@Autowired
// 替換：構造函數注入

public class NoSpringCli {
    private final OcrService ocrService;
    private final PdfService pdfService;
    
    public NoSpringCli() {
        AppConfig config = new AppConfig();
        this.ocrService = new RapidOcrServiceImpl(config);
        this.pdfService = new PdfBoxServiceImpl(config);
    }
}
```

### **3. 生命週期管理**

```java
// 當前：@PostConstruct, @PreDestroy
// 替換：手動調用

public class RapidOcrServiceImpl {
    public void initialize() {
        // 初始化邏輯
    }
    
    public void shutdown() {
        // 清理邏輯
    }
}
```

---

## 📝 **實施計劃**

### **階段 1：創建純 Java 版本**（1-2 小時）

```
1. 創建 NoSpringCli.java
2. 修改 AppConfig（移除 Spring 註解）
3. 修改 Service 實現（移除 @Service）
4. 測試功能
```

### **階段 2：優化和測試**（1 小時）

```
1. 優化配置讀取
2. 測試所有功能
3. 打包成 JAR
4. 比較性能
```

---

## 🚀 **預期結果**

### **純 Java SE 版本**

```
文件大小：20-30 MB
啟動時間：<1 秒
內存使用：50-80 MB
依賴：無框架
功能：完整（OCR + PDF）
```

---

## 💡 **建議**

### **選擇純 Java SE 版本，如果：**

```
✅ 需要最小的文件大小
✅ 需要最快的啟動速度
✅ 需要最少的內存使用
✅ 不需要 Spring Boot 的功能
✅ 願意手動管理依賴
```

### **保留 Spring Boot，如果：**

```
✅ 需要快速開發
✅ 需要自動配置
✅ 需要依賴注入
✅ 文件大小不是問題
```

---

## 🎯 **結論**

**移除 Spring Boot 是可行的，而且收益明顯！**

**預期改善：**
- 文件大小：-60-70%
- 啟動時間：-30-50%
- 內存使用：-50-70%

**工作量：**
- 修改時間：2-3 小時
- 測試時間：1 小時
- 總計：3-4 小時

---

**建議：創建新分支進行實驗！** 🧪
