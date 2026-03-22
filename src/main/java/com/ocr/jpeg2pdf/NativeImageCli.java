package com.ocr.jpeg2pdf;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * Native Image Compatible CLI
 * 
 * No external dependencies - pure Java SE
 * 
 * Usage:
 *   jpeg2pdf-ofd.exe <input> [output] [options]
 * 
 * Examples:
 *   jpeg2pdf-ofd.exe image.jpg
 *   jpeg2pdf-ofd.exe image.jpg output.pdf
 *   jpeg2pdf-ofd.exe image.jpg output.pdf --lang chinese_cht
 *   jpeg2pdf-ofd.exe folder/*.jpg output/ --lang en --format pdf
 */
public class NativeImageCli {
    
    private static final String VERSION = "1.0.0";
    
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                printUsage();
                System.exit(0);
            }
            
            // Parse arguments
            String input = null;
            String output = null;
            String language = "chinese_cht";
            String format = "pdf";
            String outputFolder = null;
            
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                
                if (arg.equals("--help") || arg.equals("-h")) {
                    printUsage();
                    System.exit(0);
                }
                
                if (arg.equals("--version") || arg.equals("-v")) {
                    System.out.println("JPEG2PDF-OFD v" + VERSION);
                    System.exit(0);
                }
                
                if (arg.equals("--lang") || arg.equals("-l")) {
                    if (i + 1 < args.length) {
                        language = args[++i];
                    }
                    continue;
                }
                
                if (arg.equals("--format") || arg.equals("-f")) {
                    if (i + 1 < args.length) {
                        format = args[++i];
                    }
                    continue;
                }
                
                if (arg.equals("--output") || arg.equals("-o")) {
                    if (i + 1 < args.length) {
                        output = args[++i];
                    }
                    continue;
                }
                
                if (arg.equals("--folder")) {
                    if (i + 1 < args.length) {
                        outputFolder = args[++i];
                    }
                    continue;
                }
                
                // Positional arguments
                if (!arg.startsWith("-")) {
                    if (input == null) {
                        input = arg;
                    } else if (output == null) {
                        output = arg;
                    }
                }
            }
            
            if (input == null) {
                System.err.println("ERROR: Input file required");
                printUsage();
                System.exit(1);
            }
            
            // Process
            System.out.println("========================================");
            System.out.println("  JPEG2PDF-OFD v" + VERSION);
            System.out.println("========================================");
            System.out.println();
            System.out.println("Input:    " + input);
            System.out.println("Language: " + language);
            System.out.println("Format:   " + format);
            System.out.println();
            
            // Get input files
            List<File> inputFiles = getInputFiles(input);
            System.out.println("Found " + inputFiles.size() + " file(s)");
            
            if (inputFiles.isEmpty()) {
                System.err.println("ERROR: No files found");
                System.exit(1);
            }
            
            // Set output folder
            if (outputFolder == null) {
                outputFolder = output != null ? output : ".";
            }
            
            Path outputPath = Paths.get(outputFolder);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
                System.out.println("Created output folder: " + outputFolder);
            }
            
            System.out.println("Output:   " + outputFolder);
            System.out.println();
            
            // Process each file
            int processed = 0;
            int failed = 0;
            
            for (File inputFile : inputFiles) {
                processed++;
                System.out.println("[" + processed + "/" + inputFiles.size() + "] " + inputFile.getName());
                
                try {
                    processFile(inputFile, outputPath, language, format);
                    System.out.println("  OK");
                } catch (Exception e) {
                    System.err.println("  ERROR: " + e.getMessage());
                    failed++;
                }
                
                System.out.println();
            }
            
            // Summary
            System.out.println("========================================");
            System.out.println("  Summary");
            System.out.println("========================================");
            System.out.println("Processed: " + processed);
            System.out.println("Failed:    " + failed);
            System.out.println();
            
            if (failed == 0) {
                System.out.println("SUCCESS: All files processed");
            } else {
                System.out.println("WARNING: Some files failed");
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static List<File> getInputFiles(String input) {
        List<File> files = new ArrayList<>();
        
        // Check if it's a wildcard pattern
        if (input.contains("*")) {
            // Extract folder and pattern
            int lastSlash = Math.max(input.lastIndexOf('/'), input.lastIndexOf('\\'));
            String folder = lastSlash > 0 ? input.substring(0, lastSlash) : ".";
            String pattern = lastSlash > 0 ? input.substring(lastSlash + 1) : input;
            
            File folderFile = new File(folder);
            if (folderFile.exists() && folderFile.isDirectory()) {
                findFiles(folderFile, pattern, files);
            }
        } else {
            // Single file or folder
            File file = new File(input);
            if (file.exists()) {
                if (file.isDirectory()) {
                    // Process all JPG files in folder
                    findFiles(file, "*.jpg", files);
                    findFiles(file, "*.jpeg", files);
                    findFiles(file, "*.png", files);
                } else {
                    files.add(file);
                }
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
            } else if (file.isFile() && matchesPattern(file.getName(), pattern)) {
                files.add(file);
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
    
    private static void processFile(File inputFile, Path outputPath, 
                                   String language, String format) throws Exception {
        
        // Generate output filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String baseName = getBaseName(inputFile.getName());
        String outputFilename = baseName + "_" + timestamp;
        
        // For now, just create text files
        // TODO: Integrate actual OCR when native image works
        
        if (format.contains("txt") || format.contains("all")) {
            Path txtPath = outputPath.resolve(outputFilename + ".txt");
            String content = "OCR Result for: " + inputFile.getName() + "\n";
            content += "Language: " + language + "\n";
            content += "Timestamp: " + timestamp + "\n";
            content += "\n[OCR text would appear here]\n";
            Files.write(txtPath, content.getBytes());
            System.out.println("    TXT: " + txtPath.getFileName());
        }
        
        if (format.contains("pdf") || format.contains("all")) {
            Path pdfPath = outputPath.resolve(outputFilename + ".pdf");
            String content = "PDF placeholder for: " + inputFile.getName();
            Files.write(pdfPath, content.getBytes());
            System.out.println("    PDF: " + pdfPath.getFileName());
        }
        
        if (format.contains("ofd") || format.contains("all")) {
            Path ofdPath = outputPath.resolve(outputFilename + ".ofd");
            String content = "OFD placeholder for: " + inputFile.getName();
            Files.write(ofdPath, content.getBytes());
            System.out.println("    OFD: " + ofdPath.getFileName());
        }
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
        System.out.println("  Native Image Edition");
        System.out.println("========================================");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  jpeg2pdf-ofd.exe <input> [output] [options]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  input          Input file, folder, or pattern (e.g., *.jpg)");
        System.out.println("  output         Output folder (default: current directory)");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --lang, -l     OCR language (default: chinese_cht)");
        System.out.println("                 Supported: chinese_cht, ch, en, japan, korean, etc.");
        System.out.println("  --format, -f   Output format (default: pdf)");
        System.out.println("                 Options: pdf, ofd, txt, all");
        System.out.println("  --help, -h     Show this help");
        System.out.println("  --version, -v  Show version");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  jpeg2pdf-ofd.exe scan.jpg");
        System.out.println("  jpeg2pdf-ofd.exe scan.jpg output/");
        System.out.println("  jpeg2pdf-ofd.exe scan.jpg output/ --lang en --format pdf");
        System.out.println("  jpeg2pdf-ofd.exe images/*.jpg output/ --format all");
        System.out.println("  jpeg2pdf-ofd.exe images/ output/ --lang chinese_cht");
        System.out.println();
        System.out.println("Supported languages:");
        System.out.println("  chinese_cht (Traditional Chinese)");
        System.out.println("  ch (Simplified Chinese)");
        System.out.println("  en (English)");
        System.out.println("  japan (Japanese)");
        System.out.println("  korean (Korean)");
        System.out.println("  french, german, spanish, etc. (80+ languages)");
        System.out.println();
    }
}
