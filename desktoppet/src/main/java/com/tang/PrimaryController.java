package com.tang;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PrimaryController {

    @FXML
    private WebView webView;

    private WebEngine engine;

    private boolean dragging;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        webView.setPageFill(javafx.scene.paint.Color.TRANSPARENT);

        engine = webView.getEngine();
        String url = getClass().getResource("index.html").toExternalForm();

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("app", this);
            }
        });
        installWindowDragging();
        engine.load(url);
    }

    private void installWindowDragging() {
        webView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            dragging = false;
            if (event.getButton() != MouseButton.PRIMARY
                    || engine.getLoadWorker().getState() != javafx.concurrent.Worker.State.SUCCEEDED) {
                return;
            }
            Point2D point = webView.sceneToLocal(event.getSceneX(), event.getSceneY());
            Object hit = engine.executeScript(
                    "(function(x,y){var orb=document.getElementById('ai-orb');"
                    + "var target=document.elementFromPoint(x,y);"
                    + "return !!orb && (target===orb || orb.contains(target));})("
                    + point.getX() + "," + point.getY() + ")");
            if (!Boolean.TRUE.equals(hit)) {
                return;
            }
            Stage stage = (Stage) webView.getScene().getWindow();
            xOffset = event.getScreenX() - stage.getX();
            yOffset = event.getScreenY() - stage.getY();
            dragging = true;
            event.consume();
        });
        webView.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (!dragging || !event.isPrimaryButtonDown()) {
                dragging = false;
                return;
            }
            Stage stage = (Stage) webView.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
            event.consume();
        });
        webView.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (dragging) {
                dragging = false;
                event.consume();
            }
        });
    }

    public void chat(String prompt) {
        OllamaClient.ask(prompt).thenAccept(reply -> {
            Platform.runLater(() -> {
                String safeReply = reply.replace("'", "\\'").replace("\n", "\\n");
                engine.executeScript(String.format("updateMessage('%s')", safeReply));
            });
        });
    }

}
