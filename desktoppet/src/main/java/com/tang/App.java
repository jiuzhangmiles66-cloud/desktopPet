package com.tang;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        // 1. 加载 FXML，注意：不要在这里写死 300, 300 这种数字！
        // 删掉数字后，窗口大小将由 FXML 内部组件（Label, ImageView等）自动撑开
        scene = new Scene(loadFXML("primary"));
        
        // 2. 设置场景背景透明（这样才没有白框）
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        
        // 3. 设置 Stage 属性
        stage.initStyle(javafx.stage.StageStyle.TRANSPARENT); // 彻底去掉系统边框
        stage.setAlwaysOnTop(true); // 让金渐层始终在最前面，陪你写代码
        
        // 4. 实现鼠标拖动（因为去掉了边框，必须手动写拖动逻辑）
        scene.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // 5. 将场景放入舞台并展示
        stage.setScene(scene);
        stage.show();
    
        System.out.println(">>> 金渐层已在桌面就绪！");
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        try {
            new ProcessBuilder("ollama", "run", "qwen2.5:7b").start();
            System.out.println(">>> 成功唤醒金渐层的大脑 (Llama 3.2)");
        } catch (Exception e) {
            System.out.println(">>> 无法自动唤醒大脑，请确保 Ollama 已安装并开启");
        }
        launch();
    }

}