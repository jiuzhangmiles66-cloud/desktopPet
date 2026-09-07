package com.tang;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import javafx.application.Platform;
import javafx.stage.Stage;

public class PrimaryController {

    @FXML private WebView webView;
    private WebEngine engine;
    
    // 记录拖动的偏移量
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        // 【关键修复】刮掉 WebView 默认的白色背景底漆！
        webView.setPageFill(javafx.scene.paint.Color.TRANSPARENT);

        engine = webView.getEngine();
        String url = getClass().getResource("index.html").toExternalForm();
        engine.load(url);

        // 建立桥梁
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("app", this);
            }
        });
    }

    // 网页中点击“发送”时会调用这里
    public void chat(String prompt) {
        OllamaClient.ask(prompt).thenAccept(reply -> {
            Platform.runLater(() -> {
                // 处理一下换行符和单引号，防止 JS 报错
                String safeReply = reply.replace("'", "\\'").replace("\n", "\\n");
                engine.executeScript(String.format("updateMessage('%s')", safeReply));
            });
        });
    }

    // --- 下面两个方法是用来接收网页传来的鼠标坐标，移动外层窗口的 ---
    public void setOffset(double x, double y) {
        Stage stage = (Stage) webView.getScene().getWindow();
        xOffset = x - stage.getX();
        yOffset = y - stage.getY();
    }

    public void dragWindow(double x, double y) {
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.setX(x - xOffset);
        stage.setY(y - yOffset);
    }
}