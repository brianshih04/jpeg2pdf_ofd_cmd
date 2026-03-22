package com.ocr.jpeg2pdf;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * JavaFX WebView 桌面应用主程序
 * 
 * 特点：
 * - 独立的 Windows 桌面应用
 * - 使用 WebKit 浏览器引擎
 * - 自动启动 Spring Boot 后端
 * - 优雅的加载动画
 * - 完整的错误处理
 * 
 * @author Brian Shih
 * @version 1.0.0
 */
@Slf4j
public class JavaFxDesktopApp extends Application {
    
    private static final String APP_NAME = "JPEG2PDF-OFD 桌面版";
    private static final String APP_URL = "http://localhost:8000";
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 800;
    private static final int SERVER_PORT = 8000;
    
    private static ConfigurableApplicationContext springContext;
    private static String[] savedArgs;
    
    private WebView webView;
    private WebEngine webEngine;
    private Stage primaryStage;
    private VBox loadingScreen;
    
    /**
     * JavaFX 启动入口
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        log.info("=====================================");
        log.info("  {} 启动中...", APP_NAME);
        log.info("=====================================");
        
        try {
            // 创建加载屏幕
            createLoadingScreen();
            
            // 配置主舞台
            Scene scene = new Scene(loadingScreen, WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setTitle(APP_NAME + " - 正在启动...");
            primaryStage.setScene(scene);
            
            // 居中显示
            centerStage(primaryStage);
            
            // 添加窗口关闭事件处理器
            primaryStage.setOnCloseRequest(this::onWindowClose);
            
            // 显示窗口
            primaryStage.show();
            
            log.info("✅ JavaFX 窗口已创建");
            
            // 在后台启动 Spring Boot
            startSpringBoot();
            
        } catch (Exception e) {
            log.error("❌ 创建窗口失败", e);
            showError("启动失败", "无法创建窗口：" + e.getMessage());
            Platform.exit();
        }
    }
    
    /**
     * 创建加载屏幕
     */
    private void createLoadingScreen() {
        Label titleLabel = new Label("JPEG2PDF-OFD 桌面版");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label statusLabel = new Label("正在启动服务，请稍候...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        progressBar.setProgress(-1); // 不确定进度
        
        VBox content = new VBox(20, titleLabel, statusLabel, progressBar);
        content.setStyle("-fx-alignment: center; -fx-padding: 40px;");
        
        loadingScreen = new StackPane(content);
        loadingScreen.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #ecf0f1 0%, #bdc3c7 100%);");
    }
    
    /**
     * 启动 Spring Boot 服务
     */
    private void startSpringBoot() {
        Task<Void> springTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    log.info("启动 Spring Boot 服务...");
                    springContext = SpringApplication.run(Application.class, savedArgs);
                    
                    log.info("✅ Spring Boot 服务启动成功（端口 {}）", SERVER_PORT);
                    
                    // 等待服务完全就绪
                    Thread.sleep(5000);
                    
                    // 验证服务是否正常
                    boolean isReady = checkServiceReady();
                    
                    if (isReady) {
                        Platform.runLater(() -> {
                            createWebView();
                            loadWebApp();
                        });
                    } else {
                        Platform.runLater(() -> {
                            showError("服务启动失败", "服务未能在指定时间内就绪");
                            Platform.exit();
                        });
                    }
                    
                } catch (Exception e) {
                    log.error("❌ Spring Boot 启动失败", e);
                    Platform.runLater(() -> {
                        showError("服务启动失败", "无法启动服务：" + e.getMessage());
                        Platform.exit();
                    });
                }
                
                return null;
            }
        };
        
        Thread thread = new Thread(springTask, "SpringBoot-Thread");
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * 检查服务是否就绪
     */
    private boolean checkServiceReady() {
        try {
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) 
                new java.net.URL(APP_URL + "/api/watch/status").openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            
            return responseCode == 200;
        } catch (Exception e) {
            log.warn("服务尚未就绪: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 创建 WebView
     */
    private void createWebView() {
        webView = new WebView();
        webEngine = webView.getEngine();
        
        // 配置 WebEngine
        webEngine.setJavaScriptEnabled(true);
        webEngine.setContextMenuEnabled(true);
        
        // 监听页面加载状态
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            log.info("页面加载状态: {} -> {}", oldValue, newValue);
            
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                log.info("✅ 页面加载成功: {}", webEngine.getLocation());
            } else if (newValue == javafx.concurrent.Worker.State.FAILED) {
                log.error("❌ 页面加载失败");
                showError("加载失败", "无法加载页面，请检查服务是否正常运行");
            }
        });
        
        // 监听页面标题变化
        webEngine.titleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                primaryStage.setTitle(APP_NAME + " - " + newValue);
            }
        });
        
        log.info("✅ WebView 已创建");
    }
    
    /**
     * 加载 Web 应用
     */
    private void loadWebApp() {
        log.info("🌐 加载 Web 应用: {}", APP_URL);
        
        // 切换场景到 WebView
        Scene webScene = new Scene(webView, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(webScene);
        primaryStage.setTitle(APP_NAME);
        
        // 加载 URL
        webEngine.load(APP_URL);
    }
    
    /**
     * 窗口关闭事件处理器
     */
    private void onWindowClose(WindowEvent event) {
        log.info("用户请求关闭窗口");
        
        // 关闭 Spring Boot
        if (springContext != null) {
            log.info("关闭 Spring Boot 服务...");
            springContext.close();
            log.info("✅ Spring Boot 已关闭");
        }
        
        // 退出应用
        Platform.exit();
        System.exit(0);
    }
    
    /**
     * 居中显示窗口
     */
    private void centerStage(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double x = (screenBounds.getWidth() - WINDOW_WIDTH) / 2;
        double y = (screenBounds.getHeight() - WINDOW_HEIGHT) / 2;
        stage.setX(x);
        stage.setY(y);
    }
    
    /**
     * 显示错误对话框
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(APP_NAME + " - 错误");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * JavaFX 停止（清理资源）
     */
    @Override
    public void stop() {
        log.info("关闭应用...");
        
        if (springContext != null) {
            springContext.close();
            log.info("✅ Spring Boot 已关闭");
        }
        
        log.info("✅ 应用已退出");
    }
    
    /**
     * 主入口
     */
    public static void main(String[] args) {
        savedArgs = args;
        launch(args);
    }
}
