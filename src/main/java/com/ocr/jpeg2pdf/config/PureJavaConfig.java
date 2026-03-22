package com.ocr.jpeg2pdf.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 純 Java SE 配置類（無 Spring Boot）
 * 
 * 支援兩種配置方式：
 * 1. JSON 文件
 * 2. Properties 文件
 */
public class PureJavaConfig {
    
    // 輸出資料夾
    private String outputFolder = getDefaultOutputFolder();
    
    // Watch 資料夾
    private String watchFolder = getDefaultWatchFolder();
    
    // 已處理資料夾
    private String saveFolder = getDefaultSaveFolder();
    
    // 暫存資料夾
    private String uploadFolder = getDefaultTempFolder();
    
    // OCR 引擎類型
    private String ocrEngine = "paddleocr";
    
    // 預設語言
    private String defaultLanguage = "chinese_cht";
    
    // PDF 字體路徑
    private String fontPath = getDefaultFontPath();
    
    // 構造函數
    public PureJavaConfig() {
        // 使用默認值
    }
    
    /**
     * 從 JSON 文件加載配置
     */
    public static PureJavaConfig loadFromJson(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("⚠️ 配置文件不存在，使用默認配置: " + filePath);
            return new PureJavaConfig();
        }
        
        PureJavaConfig config = mapper.readValue(file, PureJavaConfig.class);
        System.out.println("✅ 配置已從 JSON 加載: " + filePath);
        return config;
    }
    
    /**
     * 從 Properties 文件加載配置
     */
    public static PureJavaConfig loadFromProperties(String filePath) throws Exception {
        Properties props = new Properties();
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("⚠️ Properties 文件不存在，使用默認配置: " + filePath);
            return new PureJavaConfig();
        }
        
        try (InputStream is = new FileInputStream(file)) {
            props.load(is);
        }
        
        PureJavaConfig config = new PureJavaConfig();
        
        // 讀取配置
        if (props.containsKey("app.outputFolder")) {
            config.setOutputFolder(props.getProperty("app.outputFolder"));
        }
        if (props.containsKey("app.watchFolder")) {
            config.setWatchFolder(props.getProperty("app.watchFolder"));
        }
        if (props.containsKey("app.saveFolder")) {
            config.setSaveFolder(props.getProperty("app.saveFolder"));
        }
        if (props.containsKey("app.ocrEngine")) {
            config.setOcrEngine(props.getProperty("app.ocrEngine"));
        }
        if (props.containsKey("app.defaultLanguage")) {
            config.setDefaultLanguage(props.getProperty("app.defaultLanguage"));
        }
        if (props.containsKey("app.fontPath")) {
            config.setFontPath(props.getProperty("app.fontPath"));
        }
        
        System.out.println("✅ 配置已從 Properties 加載: " + filePath);
        return config;
    }
    
    // 默認值方法
    private String getDefaultOutputFolder() {
        return System.getProperty("user.home") + File.separator + "OCR" + File.separator + "Output";
    }
    
    private String getDefaultWatchFolder() {
        return System.getProperty("user.home") + File.separator + "OCR" + File.separator + "Watch";
    }
    
    private String getDefaultSaveFolder() {
        return System.getProperty("user.home") + File.separator + "OCR" + File.separator + "Save";
    }
    
    private String getDefaultTempFolder() {
        return System.getProperty("user.home") + File.separator + "OCR" + File.separator + "temp";
    }
    
    private String getDefaultFontPath() {
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            // Windows: 嘗試多個常見字體路徑
            String[] windowsFonts = {
                "C:/Windows/Fonts/msjh.ttc",      // 微軟正黑體
                "C:/Windows/Fonts/msyh.ttc",      // 微軟雅黑
                "C:/Windows/Fonts/simhei.ttf",    // 黑體
                "C:/Windows/Fonts/simsun.ttc"     // 宋體
            };
            
            for (String font : windowsFonts) {
                if (new File(font).exists()) {
                    return font;
                }
            }
        } else if (os.contains("mac")) {
            // macOS
            String[] macFonts = {
                "/System/Library/Fonts/PingFang.ttc",
                "/Library/Fonts/Arial Unicode.ttf"
            };
            
            for (String font : macFonts) {
                if (new File(font).exists()) {
                    return font;
                }
            }
        } else {
            // Linux
            String[] linuxFonts = {
                "/usr/share/fonts/truetype/wqy/wqy-zenhei.ttc",
                "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc"
            };
            
            for (String font : linuxFonts) {
                if (new File(font).exists()) {
                    return font;
                }
            }
        }
        
        // 如果都找不到，返回 null（PDFBox 會使用默認字體）
        return null;
    }
    
    // Getters & Setters
    
    public String getOutputFolder() {
        return outputFolder;
    }
    
    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }
    
    public String getWatchFolder() {
        return watchFolder;
    }
    
    public void setWatchFolder(String watchFolder) {
        this.watchFolder = watchFolder;
    }
    
    public String getSaveFolder() {
        return saveFolder;
    }
    
    public void setSaveFolder(String saveFolder) {
        this.saveFolder = saveFolder;
    }
    
    public String getUploadFolder() {
        return uploadFolder;
    }
    
    public void setUploadFolder(String uploadFolder) {
        this.uploadFolder = uploadFolder;
    }
    
    public String getOcrEngine() {
        return ocrEngine;
    }
    
    public void setOcrEngine(String ocrEngine) {
        this.ocrEngine = ocrEngine;
    }
    
    public String getDefaultLanguage() {
        return defaultLanguage;
    }
    
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
    
    public String getFontPath() {
        return fontPath;
    }
    
    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }
}
