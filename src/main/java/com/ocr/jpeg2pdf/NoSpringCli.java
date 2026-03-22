package com.ocr.jpeg2pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocr.jpeg2pdf.config.PureJavaConfig;
import com.ocr.jpeg2pdf.model.OcrResult;
import com.ocr.jpeg2pdf.service.impl.RapidOcrServiceImpl;
import com.ocr.jpeg2pdf.service.impl.PdfBoxServiceImpl;
import com.ocr.jpeg2pdf.service.impl.TextServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 完全不依賴 Spring Boot 的純 Java SE 版本
 * 
 * 特點：
 * - 無 Spring Boot 框架
 * - 無 Web Server
 * - 無依賴注入框架
 * - 手動管理 Service 實例
 * - 預期：更小、更快、更輕量
 * 
 * @author Brian Shih
 * @version 3.0.0
 */
public class NoSpringCli {
    
    private static final String VERSION = "3.0.0 (No Spring Boot)";
    
    // Service 實例（手動管理）
    private final RapidOcrServiceImpl ocrService;
    private final PdfBoxServiceImpl pdfService;
    private final TextServiceImpl textService;
    private final PureJavaConfig config;
    
    /**
     * 構造函數：手動創建所有 Service
     */
    public NoSpringCli() {
        // 創建配置
        this.config = new PureJavaConfig();
        
        // 手動創建 Service 實例（無 @Autowired）
        this.ocrService = new RapidOcrServiceImpl(this.config);
        this.pdfService = new PdfBoxServiceImpl(this.config);
        this.textService = new TextServiceImpl();
        
        System.out.println("✅ Service 實例已創建（無 Spring Boot）");
    }
    
    public static void main(String[] args) {
        try {
            // 設置 headless 模式
            System.setProperty("java.awt.headless", "true");
            
            if (args.length == 0) {
                printUsage();
                System.exit(0);
            }
            
            String configFile = args[0];
            
            // 處理特殊命令
            if (configFile.equals("--help") || configFile.equals("-h")) {
                printUsage();
                System.exit(0);
            }
            
            if (configFile.equals("--version") || configFile.equals("-v")) {
                System.out.println("JPEG2PDF-OFD v" + VERSION);
                System.exit(0);
            }
            
            // 創建實例並運行
            NoSpringCli cli = new NoSpringCli();
            cli.run(configFile);
            
        } catch (Exception e) {
            System.err.println("❌ 錯誤: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private void run(String configFile) throws Exception {
        // 加載配置文件
        System.out.println("========================================");
        System.out.println("  JPEG2PDF-OFD v" + VERSION);
        System.out.println("========================================");
        System.out.println();
        System.out.println("配置文件: " + configFile);
        
        File file = new File(configFile);
        if (!file.exists()) {
            System.err.println("❌ 配置文件不存在: " + configFile);
            System.exit(1);
        }
        
        // 解析配置（使用 Jackson，無 Spring）
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> config = mapper.readValue(file, Map.class);
        System.out.println("✅ 配置文件已加載");
        
        // 執行轉換
        processConfig(config);
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("  ✅ 轉換完成！");
        System.out.println("========================================");
    }
    
    private void processConfig(Map<String, Object> config) throws Exception {
        // 獲取輸入配置
        Map<String, Object> inputConfig = (Map<String, Object>) config.get("input");
        Map<String, Object> outputConfig = (Map<String, Object>) config.get("output");
        Map<String, Object> ocrConfig = (Map<String, Object>) config.get("ocr");
        
        // 獲取輸入文件
        List<File> inputFiles = getInputFiles(inputConfig);
        System.out.println("找到 " + inputFiles.size() + " 個文件");
        
        if (inputFiles.isEmpty()) {
            System.err.println("❌ 沒有找到文件");
            return;
        }
        
        // 獲取輸出配置
        String outputFolder = getOutputFolder(outputConfig);
        String format = getOutputFormat(outputConfig);
        
        // 獲取 OCR 語言
        String language = getOcrLanguage(ocrConfig);
        
        // 創建輸出目錄
        Path outputPath = Paths.get(outputFolder);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        // 處理每個文件
        int processed = 0;
        for (File inputFile : inputFiles) {
            processed++;
            System.out.println();
            System.out.println("[" + processed + "/" + inputFiles.size() + "] 處理: " + inputFile.getName());
            
            try {
                // 加載圖片
                BufferedImage image = ImageIO.read(inputFile);
                if (image == null) {
                    System.err.println("  ❌ 無法讀取圖片文件");
                    continue;
                }
                
                System.out.println("  圖片尺寸: " + image.getWidth() + "x" + image.getHeight());
                
                // OCR 識別
                System.out.println("  OCR 識別中...");
                List<BufferedImage> images = new ArrayList<>();
                images.add(image);
                
                List<OcrResult> ocrResults = ocrService.batchRecognize(images, language);
                
                if (ocrResults == null || ocrResults.isEmpty()) {
                    System.err.println("  ❌ OCR 識別失敗");
                    continue;
                }
                
                System.out.println("  ✅ OCR 完成 (識別 " + ocrResults.size() + " 行文字)");
                
                // 生成輸出文件名
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String baseName = getBaseName(inputFile.getName());
                String outputFilename = baseName + "_" + timestamp;
                
                // 導出文件
                exportFiles(ocrResults.get(0), outputFolder, outputFilename, format);
                
                System.out.println("  ✅ 文件已導出");
                
            } catch (Exception e) {
                System.err.println("  ❌ 處理失敗: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private List<File> getInputFiles(Map<String, Object> inputConfig) {
        List<File> files = new ArrayList<>();
        
        if (inputConfig == null) {
            return files;
        }
        
        // 單個文件
        if (inputConfig.containsKey("file")) {
            String filePath = (String) inputConfig.get("file");
            File file = new File(filePath);
            if (file.exists()) {
                files.add(file);
            }
            return files;
        }
        
        // 資料夾
        if (inputConfig.containsKey("folder")) {
            String folderPath = (String) inputConfig.get("folder");
            String pattern = inputConfig.containsKey("pattern") 
                ? (String) inputConfig.get("pattern") 
                : "*.jpg";
            
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                findFiles(folder, pattern, files);
            }
        }
        
        return files;
    }
    
    private void findFiles(File folder, String pattern, List<File> files) {
        File[] fileList = folder.listFiles();
        if (fileList == null) return;
        
        for (File file : fileList) {
            if (file.isDirectory()) {
                findFiles(file, pattern, files);
            } else if (file.isFile() && matchesPattern(file.getName(), pattern)) {
                files.add(file);
            }
        }
    }
    
    private boolean matchesPattern(String filename, String pattern) {
        if (pattern.equals("*") || pattern.equals("*.*")) {
            return true;
        }
        
        if (pattern.startsWith("*.")) {
            String ext = pattern.substring(1).toLowerCase();
            return filename.toLowerCase().endsWith(ext);
        }
        
        return filename.equals(pattern);
    }
    
    private String getOutputFolder(Map<String, Object> outputConfig) {
        if (outputConfig != null && outputConfig.containsKey("folder")) {
            return (String) outputConfig.get("folder");
        }
        return ".";
    }
    
    private String getOutputFormat(Map<String, Object> outputConfig) {
        if (outputConfig != null && outputConfig.containsKey("format")) {
            Object format = outputConfig.get("format");
            if (format instanceof String) {
                return (String) format;
            } else if (format instanceof List) {
                return String.join(",", (List<String>) format);
            }
        }
        return "pdf";
    }
    
    private String getOcrLanguage(Map<String, Object> ocrConfig) {
        if (ocrConfig != null && ocrConfig.containsKey("language")) {
            return (String) ocrConfig.get("language");
        }
        return "chinese_cht";
    }
    
    private void exportFiles(OcrResult ocrResult, String outputFolder, 
                            String outputFilename, String format) throws Exception {
        Path outputPath = Paths.get(outputFolder);
        
        // PDF
        if (format.contains("pdf") || format.contains("all")) {
            Path pdfPath = outputPath.resolve(outputFilename + ".pdf");
            List<BufferedImage> images = new ArrayList<>();
            List<OcrResult> results = new ArrayList<>();
            results.add(ocrResult);
            
            pdfService.generateSearchablePdf(images, results, pdfPath);
            System.out.println("    ✅ PDF: " + pdfPath.getFileName());
        }
        
        // TXT
        if (format.contains("txt") || format.contains("all")) {
            Path txtPath = outputPath.resolve(outputFilename + ".txt");
            List<OcrResult> results = new ArrayList<>();
            results.add(ocrResult);
            
            textService.exportText(results, txtPath);
            System.out.println("    ✅ TXT: " + txtPath.getFileName());
        }
        
        // OFD（暫時跳過，有問題）
        if (format.contains("ofd")) {
            System.out.println("    ⚠️ OFD: 暫不支持");
        }
    }
    
    private String getBaseName(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            return filename.substring(0, dotIndex);
        }
        return filename;
    }
    
    private static void printUsage() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("  JPEG2PDF-OFD v" + VERSION);
        System.out.println("========================================");
        System.out.println();
        System.out.println("純 Java SE 版本 - 無 Spring Boot");
        System.out.println();
        System.out.println("特點：");
        System.out.println("  ✅ 無框架依賴");
        System.out.println("  ✅ 更小（預計 20-30 MB）");
        System.out.println("  ✅ 更快（<1 秒啟動）");
        System.out.println("  ✅ 更輕（50-80 MB 內存）");
        System.out.println();
        System.out.println("用法:");
        System.out.println("  java -jar jpeg2pdf-ofd.jar config.json");
        System.out.println();
        System.out.println("選項:");
        System.out.println("  --help, -h      顯示幫助");
        System.out.println("  --version, -v   顯示版本");
        System.out.println();
        System.out.println("配置示例:");
        System.out.println("  {");
        System.out.println("    \"input\": {");
        System.out.println("      \"folder\": \"C:/OCR/Watch\",");
        System.out.println("      \"pattern\": \"*.jpg\"");
        System.out.println("    },");
        System.out.println("    \"output\": {");
        System.out.println("      \"folder\": \"C:/OCR/Output\",");
        System.out.println("      \"format\": \"pdf\"");
        System.out.println("    },");
        System.out.println("    \"ocr\": {");
        System.out.println("      \"language\": \"chinese_cht\"");
        System.out.println("    }");
        System.out.println("  }");
        System.out.println();
    }
}
