package com.ocr.jpeg2pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocr.jpeg2pdf.model.OcrResult;
import com.ocr.jpeg2pdf.service.OcrService;
import com.ocr.jpeg2pdf.service.PdfService;
import com.ocr.jpeg2pdf.service.OfdService;
import com.ocr.jpeg2pdf.service.TextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
 * 命令行版本主程序
 * 
 * 用法：
 * java -jar jpeg2pdf-ofd.jar config.json
 * 
 * 或打包成 EXE 后：
 * jpeg2pdf-ofd.exe config.json
 * 
 * @author Brian Shih
 * @version 1.0.0
 */
@Slf4j
@SpringBootApplication
public class CliApplication implements CommandLineRunner {
    
    private final OcrService ocrService;
    private final PdfService pdfService;
    private final OfdService ofdService;
    private final TextService textService;
    private final ObjectMapper objectMapper;
    
    public CliApplication(OcrService ocrService, 
                          PdfService pdfService,
                          OfdService ofdService,
                          TextService textService) {
        this.ocrService = ocrService;
        this.pdfService = pdfService;
        this.ofdService = ofdService;
        this.textService = textService;
        this.objectMapper = new ObjectMapper();
    }
    
    public static void main(String[] args) {
        // 禁用 Spring Boot 的图形界面
        System.setProperty("java.awt.headless", "true");
        
        SpringApplication.run(CliApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        try {
            if (args.length == 0) {
                printUsage();
                System.exit(0);
            }
            
            String configFile = args[0];
            
            // 处理特殊命令
            if (configFile.equals("--help") || configFile.equals("-h")) {
                printUsage();
                System.exit(0);
            }
            
            if (configFile.equals("--version") || configFile.equals("-v")) {
                printVersion();
                System.exit(0);
            }
            
            // 加载配置文件
            log.info("========================================");
            log.info("  JPEG2PDF-OFD 命令行版本 v1.0.0");
            log.info("========================================");
            log.info("");
            log.info("配置文件: {}", configFile);
            
            File file = new File(configFile);
            if (!file.exists()) {
                log.error("❌ 配置文件不存在: {}", configFile);
                System.exit(1);
            }
            
            // 解析配置
            Map<String, Object> config = objectMapper.readValue(file, Map.class);
            log.info("✅ 配置文件已加载");
            
            // 执行转换
            processConfig(config);
            
            log.info("");
            log.info("========================================");
            log.info("  ✅ 转换完成！");
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("❌ 转换失败", e);
            System.exit(1);
        }
    }
    
    private void processConfig(Map<String, Object> config) throws Exception {
        // 获取输入配置
        Map<String, Object> inputConfig = (Map<String, Object>) config.get("input");
        Map<String, Object> outputConfig = (Map<String, Object>) config.get("output");
        Map<String, Object> ocrConfig = (Map<String, Object>) config.get("ocr");
        
        // 获取输入文件
        List<File> inputFiles = getInputFiles(inputConfig);
        log.info("找到 {} 个文件", inputFiles.size());
        
        // 获取输出配置
        String outputFolder = getOutputFolder(outputConfig);
        String format = getOutputFormat(outputConfig);
        String filenameTemplate = getFilenameTemplate(outputConfig);
        
        // 获取 OCR 语言
        String language = getOcrLanguage(ocrConfig);
        
        // 处理每个文件
        int processed = 0;
        for (File inputFile : inputFiles) {
            processed++;
            log.info("");
            log.info("[{}/{}] 处理: {}", processed, inputFiles.size(), inputFile.getName());
            
            try {
                // 加载图片
                BufferedImage image = ImageIO.read(inputFile);
                if (image == null) {
                    log.error("  ❌ 无法读取图片文件");
                    continue;
                }
                
                log.info("  图片尺寸: {}x{}", image.getWidth(), image.getHeight());
                
                // OCR 识别
                log.info("  OCR 识别中...");
                List<BufferedImage> images = new ArrayList<>();
                images.add(image);
                
                List<OcrResult> ocrResults = ocrService.batchRecognize(images, language);
                
                if (ocrResults.isEmpty()) {
                    log.error("  ❌ OCR 识别失败");
                    continue;
                }
                
                OcrResult ocrResult = ocrResults.get(0);
                log.info("  ✅ OCR 完成 (识别 {} 行文字)", ocrResult.getTextPositions().size());
                
                // 生成输出文件名
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String baseName = getBaseName(inputFile.getName());
                
                // 导出文件
                exportFiles(ocrResults, outputFolder, baseName, timestamp, format);
                
                log.info("  ✅ 文件已导出");
                
            } catch (Exception e) {
                log.error("  ❌ 处理失败: {}", e.getMessage());
            }
        }
    }
    
    private List<File> getInputFiles(Map<String, Object> inputConfig) {
        List<File> files = new ArrayList<>();
        
        if (inputConfig == null) {
            log.error("❌ 缺少 input 配置");
            return files;
        }
        
        // 单个文件
        if (inputConfig.containsKey("file")) {
            String filePath = (String) inputConfig.get("file");
            File file = new File(filePath);
            if (file.exists()) {
                files.add(file);
            }
            return files;
        }
        
        // 文件夹
        if (inputConfig.containsKey("folder")) {
            String folderPath = (String) inputConfig.get("folder");
            String pattern = (String) inputConfig.getOrDefault("pattern", "*.jpg");
            boolean recursive = (Boolean) inputConfig.getOrDefault("recursive", false);
            
            File folder = new File(folderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                log.error("❌ 文件夹不存在: {}", folderPath);
                return files;
            }
            
            // 查找文件
            findFiles(folder, pattern, recursive, files);
        }
        
        return files;
    }
    
    private void findFiles(File folder, String pattern, boolean recursive, List<File> files) {
        File[] fileList = folder.listFiles();
        if (fileList == null) return;
        
        for (File file : fileList) {
            if (file.isDirectory() && recursive) {
                findFiles(file, pattern, recursive, files);
            } else if (file.isFile()) {
                if (matchesPattern(file.getName(), pattern)) {
                    files.add(file);
                }
            }
        }
    }
    
    private boolean matchesPattern(String filename, String pattern) {
        if (pattern.equals("*") || pattern.equals("*.*")) {
            return true;
        }
        
        if (pattern.startsWith("*.")) {
            String ext = pattern.substring(1);
            return filename.toLowerCase().endsWith(ext.toLowerCase());
        }
        
        return filename.equals(pattern);
    }
    
    private String getOutputFolder(Map<String, Object> outputConfig) {
        if (outputConfig == null) {
            return ".";
        }
        
        String folder = (String) outputConfig.getOrDefault("folder", ".");
        Path path = Paths.get(folder);
        
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            log.warn("无法创建输出文件夹，使用当前目录");
            return ".";
        }
        
        return folder;
    }
    
    private String getOutputFormat(Map<String, Object> outputConfig) {
        if (outputConfig == null) {
            return "pdf";
        }
        
        Object format = outputConfig.get("format");
        if (format == null) {
            return "pdf";
        }
        
        if (format instanceof String) {
            return (String) format;
        }
        
        if (format instanceof List) {
            return String.join(",", ((List<String>) format));
        }
        
        return "pdf";
    }
    
    private String getFilenameTemplate(Map<String, Object> outputConfig) {
        if (outputConfig == null) {
            return "{original}_{timestamp}";
        }
        
        return (String) outputConfig.getOrDefault("filenameTemplate", "{original}_{timestamp}");
    }
    
    private String getOcrLanguage(Map<String, Object> ocrConfig) {
        if (ocrConfig == null) {
            return "chinese_cht";
        }
        
        return (String) ocrConfig.getOrDefault("language", "chinese_cht");
    }
    
    private void exportFiles(List<OcrResult> ocrResults, String outputFolder, 
                            String baseName, String timestamp, String format) throws Exception {
        String outputFilename = baseName + "_" + timestamp;
        Path outputPath = Paths.get(outputFolder);
        
        // 准备图片列表
        List<BufferedImage> images = new ArrayList<>();
        for (OcrResult result : ocrResults) {
            if (result.getImageId() != null) {
                // 从临时文件夹加载图片
                Path imagePath = Paths.get("C:/OCR/temp", result.getImageId() + ".png");
                if (Files.exists(imagePath)) {
                    BufferedImage image = ImageIO.read(imagePath.toFile());
                    if (image != null) {
                        images.add(image);
                    }
                }
            }
        }
        
        // PDF
        if (format.contains("pdf") || format.contains("all")) {
            Path pdfPath = outputPath.resolve(outputFilename + ".pdf");
            pdfService.generateSearchablePdf(images, ocrResults, pdfPath);
            log.info("    📄 PDF: {}", pdfPath.getFileName());
        }
        
        // OFD
        if (format.contains("ofd") || format.contains("all")) {
            Path ofdPath = outputPath.resolve(outputFilename + ".ofd");
            ofdService.generateSearchableOfd(images, ocrResults, ofdPath);
            log.info("    📄 OFD: {}", ofdPath.getFileName());
        }
        
        // TXT
        if (format.contains("txt") || format.contains("all")) {
            Path txtPath = outputPath.resolve(outputFilename + ".txt");
            textService.exportText(ocrResults, txtPath);
            log.info("    📄 TXT: {}", txtPath.getFileName());
        }
    }
    
    private String getBaseName(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            return filename.substring(0, dotIndex);
        }
        return filename;
    }
    
    private void printUsage() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("  JPEG2PDF-OFD 命令行版本 v1.0.0");
        System.out.println("========================================");
        System.out.println();
        System.out.println("用法：");
        System.out.println("  jpeg2pdf-ofd.exe <config.json>");
        System.out.println();
        System.out.println("示例：");
        System.out.println("  jpeg2pdf-ofd.exe config-simple.json");
        System.out.println("  jpeg2pdf-ofd.exe config-batch.json");
        System.out.println();
        System.out.println("选项：");
        System.out.println("  --help, -h      显示帮助信息");
        System.out.println("  --version, -v   显示版本信息");
        System.out.println();
        System.out.println("配置文件示例：");
        System.out.println("  {");
        System.out.println("    \"input\": \"image.jpg\",");
        System.out.println("    \"output\": {");
        System.out.println("      \"folder\": \"./output\",");
        System.out.println("      \"format\": \"pdf\"");
        System.out.println("    },");
        System.out.println("    \"ocr\": {");
        System.out.println("      \"language\": \"chinese_cht\"");
        System.out.println("    }");
        System.out.println("  }");
        System.out.println();
        System.out.println("更多信息：https://github.com/brianshih04/jpeg2pdf_ofd_cmd");
        System.out.println();
    }
    
    private void printVersion() {
        System.out.println("JPEG2PDF-OFD v1.0.0");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
    }
}
