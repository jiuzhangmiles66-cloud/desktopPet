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

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"));

        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        stage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);

        stage.setScene(scene);
        stage.show();

        System.out.println(">>> Desktop pet is ready!");
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                App.class.getResource(fxml + ".fxml")
        );
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        try {
            new ProcessBuilder("ollama", "run", "qwen2.5:7b").start();
            System.out.println(">>> Ollama launch command sent for Qwen 2.5 7B.");
        } catch (Exception e) {
            System.out.println(
                    ">>> Failed to launch Ollama. Please ensure it is installed and running."
            );
        }

        launch();
    }
}