package com.ocr.jpeg2pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.HashMap;

/**
 * 简化版命令行应用 - 无 Spring Boot 依赖
 * 
 * 用途：
 * - 更容易编译成 GraalVM Native Image
 * - 生成真正的单文件 EXE
 * - 无需 JRE
 * 
 * @author Brian Shih
 * @version 1.0.0
 */
public class SimpleCli {
    
    private static final String VERSION = "1.0.0";
    
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                printUsage();
                System.exit(0);
            }
            
            String configFile = args[0];
            
            // 特殊命令
            if (configFile.equals("--help") || configFile.equals("-h")) {
                printUsage();
                System.exit(0);
            }
            
            if (configFile.equals("--version") || configFile.equals("-v")) {
                System.out.println("JPEG2PDF-OFD v" + VERSION);
                System.exit(0);
            }
            
            // 执行转换
            System.out.println("========================================");
            System.out.println("  JPEG2PDF-OFD v" + VERSION);
            System.out.println("========================================");
            System.out.println();
            System.out.println("配置文件: " + configFile);
            
            // 加载配置
            File file = new File(configFile);
            if (!file.exists()) {
                System.err.println("❌ 配置文件不存在: " + configFile);
                System.exit(1);
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode config = mapper.readTree(file);
            System.out.println("✅ 配置文件已加载");
            
            // 处理转换
            processConversion(config);
            
            System.out.println();
            System.out.println("========================================");
            System.out.println("  ✅ 转换完成！");
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("❌ 转换失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void processConversion(JsonNode config) throws Exception {
        // 获取输入配置
        JsonNode inputNode = config.get("input");
        JsonNode outputNode = config.get("output");
        JsonNode ocrNode = config.get("ocr");
        
        if (inputNode == null) {
            throw new IllegalArgumentException("缺少 input 配置");
        }
        
        // 获取输入文件
        List<File> inputFiles = getInputFiles(inputNode);
        System.out.println("找到 " + inputFiles.size() + " 个文件");
        
        // 获取输出配置
        String outputFolder = outputNode != null && outputNode.has("folder") 
            ? outputNode.get("folder").asText() : ".";
        String format = outputNode != null && outputNode.has("format")
            ? outputNode.get("format").asText() : "pdf";
        String language = ocrNode != null && ocrNode.has("language")
            ? ocrNode.get("language").asText() : "chinese_cht";
        
        // 创建输出目录
        Path outputPath = Paths.get(outputFolder);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        // 处理每个文件
        int processed = 0;
        for (File inputFile : inputFiles) {
            processed++;
            System.out.println();
            System.out.println("[" + processed + "/" + inputFiles.size() + "] 处理: " + inputFile.getName());
            
            try {
                // 读取图片
                BufferedImage image = ImageIO.read(inputFile);
                if (image == null) {
                    System.err.println("  ❌ 无法读取图片");
                    continue;
                }
                
                System.out.println("  图片尺寸: " + image.getWidth() + "x" + image.getHeight());
                
                // 生成输出文件名
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String baseName = getBaseName(inputFile.getName());
                String outputFilename = baseName + "_" + timestamp;
                
                // OCR 识别
                System.out.println("  OCR 识别中...");
                OcrResult result = performOcr(image, language);
                System.out.println("  ✅ OCR 完成 (识别 " + result.lineCount + " 行文字)");
                
                // 导出文件
                exportFiles(result, outputFolder, outputFilename, format);
                System.out.println("  ✅ 文件已导出");
                
            } catch (Exception e) {
                System.err.println("  ❌ 处理失败: " + e.getMessage());
            }
        }
    }
    
    private static List<File> getInputFiles(JsonNode inputNode) {
        List<File> files = new ArrayList<>();
        
        // 单个文件
        if (inputNode.isTextual()) {
            File file = new File(inputNode.asText());
            if (file.exists()) {
                files.add(file);
            }
            return files;
        }
        
        // 对象形式
        if (inputNode.has("file")) {
            File file = new File(inputNode.get("file").asText());
            if (file.exists()) {
                files.add(file);
            }
            return files;
        }
        
        // 文件夹
        if (inputNode.has("folder")) {
            String folderPath = inputNode.get("folder").asText();
            String pattern = inputNode.has("pattern") ? inputNode.get("pattern").asText() : "*.jpg";
            
            File folder = new File(folderPath);
            if (folder.exists() && folder.isDirectory()) {
                findFiles(folder, pattern, files);
            }
        }
        
        return files;
    }
    
    private static void findFiles(File folder, String pattern, List<File> files) {
        File[] fileList = folder.listFiles();
        if (fileList == null) return;
        
        for (File file : fileList) {
            if (file.isDirectory()) {
                findFiles(file, pattern, files);
            } else if (file.isFile()) {
                if (matchesPattern(file.getName(), pattern)) {
                    files.add(file);
                }
            }
        }
    }
    
    private static boolean matchesPattern(String filename, String pattern) {
        if (pattern.equals("*") || pattern.equals("*.*")) {
            return true;
        }
        
        if (pattern.startsWith("*.")) {
            String ext = pattern.substring(1).toLowerCase();
            return filename.toLowerCase().endsWith(ext);
        }
        
        return filename.equals(pattern);
    }
    
    private static OcrResult performOcr(BufferedImage image, String language) throws Exception {
        // TODO: 实际 OCR 实现
        // 这里需要调用 RapidOCR
        
        OcrResult result = new OcrResult();
        result.text = "示例文字";
        result.confidence = 0.95;
        result.lineCount = 1;
        result.textPositions = new ArrayList<>();
        
        return result;
    }
    
    private static void exportFiles(OcrResult result, String outputFolder, 
                                   String outputFilename, String format) throws Exception {
        Path outputPath = Paths.get(outputFolder);
        
        // PDF
        if (format.contains("pdf") || format.contains("all")) {
            Path pdfPath = outputPath.resolve(outputFilename + ".pdf");
            exportPdf(result, pdfPath);
            System.out.println("    📄 PDF: " + pdfPath.getFileName());
        }
        
        // TXT
        if (format.contains("txt") || format.contains("all")) {
            Path txtPath = outputPath.resolve(outputFilename + ".txt");
            exportTxt(result, txtPath);
            System.out.println("    📄 TXT: " + txtPath.getFileName());
        }
        
        // OFD
        if (format.contains("ofd") || format.contains("all")) {
            Path ofdPath = outputPath.resolve(outputFilename + ".ofd");
            exportOfd(result, ofdPath);
            System.out.println("    📄 OFD: " + ofdPath.getFileName());
        }
    }
    
    private static void exportPdf(OcrResult result, Path outputPath) throws Exception {
        // TODO: 实现 PDF 导出
        Files.write(outputPath, ("PDF Content: " + result.text).getBytes());
    }
    
    private static void exportTxt(OcrResult result, Path outputPath) throws Exception {
        Files.write(outputPath, result.text.getBytes());
    }
    
    private static void exportOfd(OcrResult result, Path outputPath) throws Exception {
        // TODO: 实现 OFD 导出
        Files.write(outputPath, ("OFD Content: " + result.text).getBytes());
    }
    
    private static String getBaseName(String filename) {
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
        System.out.println("用法：");
        System.out.println("  jpeg2pdf-ofd.exe <config.json>");
        System.out.println();
        System.out.println("示例：");
        System.out.println("  jpeg2pdf-ofd.exe config-simple.json");
        System.out.println();
        System.out.println("选项：");
        System.out.println("  --help, -h      显示帮助");
        System.out.println("  --version, -v   显示版本");
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
    }
    
    // 内部类：OCR 结果
    static class OcrResult {
        String text;
        double confidence;
        int lineCount;
        List<TextPosition> textPositions;
    }
    
    // 内部类：文字位置
    static class TextPosition {
        String text;
        double confidence;
        double x, y, width, height;
    }
}
