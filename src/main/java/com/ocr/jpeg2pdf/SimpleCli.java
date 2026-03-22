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

/**
 * Simple CLI Application for OCR
 * No Spring Boot dependencies for easier Native Image compilation
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
            
            if (configFile.equals("--help") || configFile.equals("-h")) {
                printUsage();
                System.exit(0);
            }
            
            if (configFile.equals("--version") || configFile.equals("-v")) {
                System.out.println("JPEG2PDF-OFD v" + VERSION);
                System.exit(0);
            }
            
            System.out.println("========================================");
            System.out.println("  JPEG2PDF-OFD v" + VERSION);
            System.out.println("========================================");
            System.out.println();
            System.out.println("Config file: " + configFile);
            
            File file = new File(configFile);
            if (!file.exists()) {
                System.err.println("ERROR: Config file not found: " + configFile);
                System.exit(1);
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode config = mapper.readTree(file);
            System.out.println("OK: Config file loaded");
            
            processConversion(config);
            
            System.out.println();
            System.out.println("========================================");
            System.out.println("  Conversion completed!");
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("ERROR: Conversion failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void processConversion(JsonNode config) throws Exception {
        JsonNode inputNode = config.get("input");
        JsonNode outputNode = config.get("output");
        JsonNode ocrNode = config.get("ocr");
        
        if (inputNode == null) {
            throw new IllegalArgumentException("Missing input configuration");
        }
        
        List<File> inputFiles = getInputFiles(inputNode);
        System.out.println("Found " + inputFiles.size() + " file(s)");
        
        String outputFolder = outputNode != null && outputNode.has("folder") 
            ? outputNode.get("folder").asText() : ".";
        String format = outputNode != null && outputNode.has("format")
            ? outputNode.get("format").asText() : "pdf";
        String language = ocrNode != null && ocrNode.has("language")
            ? ocrNode.get("language").asText() : "chinese_cht";
        
        Path outputPath = Paths.get(outputFolder);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        int processed = 0;
        for (File inputFile : inputFiles) {
            processed++;
            System.out.println();
            System.out.println("[" + processed + "/" + inputFiles.size() + "] Processing: " + inputFile.getName());
            
            try {
                BufferedImage image = ImageIO.read(inputFile);
                if (image == null) {
                    System.err.println("  ERROR: Cannot read image");
                    continue;
                }
                
                System.out.println("  Image size: " + image.getWidth() + "x" + image.getHeight());
                
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String baseName = getBaseName(inputFile.getName());
                String outputFilename = baseName + "_" + timestamp;
                
                System.out.println("  Running OCR...");
                OcrResult result = performOcr(image, language);
                System.out.println("  OK: OCR completed (" + result.lineCount + " lines)");
                
                exportFiles(result, outputFolder, outputFilename, format);
                System.out.println("  OK: Files exported");
                
            } catch (Exception e) {
                System.err.println("  ERROR: Processing failed: " + e.getMessage());
            }
        }
    }
    
    private static List<File> getInputFiles(JsonNode inputNode) {
        List<File> files = new ArrayList<>();
        
        if (inputNode.isTextual()) {
            File file = new File(inputNode.asText());
            if (file.exists()) {
                files.add(file);
            }
            return files;
        }
        
        if (inputNode.has("file")) {
            File file = new File(inputNode.get("file").asText());
            if (file.exists()) {
                files.add(file);
            }
            return files;
        }
        
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
        // TODO: Implement actual OCR
        OcrResult result = new OcrResult();
        result.text = "Sample text";
        result.confidence = 0.95;
        result.lineCount = 1;
        result.textPositions = new ArrayList<>();
        
        return result;
    }
    
    private static void exportFiles(OcrResult result, String outputFolder, 
                                   String outputFilename, String format) throws Exception {
        Path outputPath = Paths.get(outputFolder);
        
        if (format.contains("pdf") || format.contains("all")) {
            Path pdfPath = outputPath.resolve(outputFilename + ".pdf");
            exportPdf(result, pdfPath);
            System.out.println("    PDF: " + pdfPath.getFileName());
        }
        
        if (format.contains("txt") || format.contains("all")) {
            Path txtPath = outputPath.resolve(outputFilename + ".txt");
            exportTxt(result, txtPath);
            System.out.println("    TXT: " + txtPath.getFileName());
        }
        
        if (format.contains("ofd") || format.contains("all")) {
            Path ofdPath = outputPath.resolve(outputFilename + ".ofd");
            exportOfd(result, ofdPath);
            System.out.println("    OFD: " + ofdPath.getFileName());
        }
    }
    
    private static void exportPdf(OcrResult result, Path outputPath) throws Exception {
        Files.write(outputPath, ("PDF Content: " + result.text).getBytes());
    }
    
    private static void exportTxt(OcrResult result, Path outputPath) throws Exception {
        Files.write(outputPath, result.text.getBytes());
    }
    
    private static void exportOfd(OcrResult result, Path outputPath) throws Exception {
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
        System.out.println("Usage:");
        System.out.println("  jpeg2pdf-ofd.exe <config.json>");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  jpeg2pdf-ofd.exe config-simple.json");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --help, -h      Show help");
        System.out.println("  --version, -v   Show version");
        System.out.println();
        System.out.println("Config example:");
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
    
    static class OcrResult {
        String text;
        double confidence;
        int lineCount;
        List<TextPosition> textPositions;
    }
    
    static class TextPosition {
        String text;
        double confidence;
        double x, y, width, height;
    }
}
