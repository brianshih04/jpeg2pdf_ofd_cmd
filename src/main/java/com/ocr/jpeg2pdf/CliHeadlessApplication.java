package com.ocr.jpeg2pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 純命令行版本 - 不啟動 Web Server
 *
 * @author Brian Shih
 * @version 1.0.0
 */
@SpringBootApplication
public class CliHeadlessApplication {

    public static void main(String[] args) {
        // 設置為 headless 模式
        System.setProperty("java.awt.headless", "true");

        // 創建 Spring Application 並禁用 Web Server
        new SpringApplicationBuilder(CliHeadlessApplication.class)
            .web(WebApplicationType.NONE)  // 不啟動 Web Server
            .run(args);
    }
}
