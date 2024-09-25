package cl.jic.VeloPro.Utility;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class ButtonManager {

    public Button createButton(String imageName, String buttonColor, int width, int height) {
        Button button = new Button();
        Image image = new Image("Images/Icons/" + imageName);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        button.setCursor(Cursor.HAND);
        button.setStyle("-fx-background-color: " + buttonColor + ";");
        button.setTextFill(Color.WHITE);
        button.setGraphic(imageView);
        return button;
    }

    public void selectedButtonStage(Button button, Scene scene, Stage stage){
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                stage.close();
                button.getStyleClass().remove("custom-button-selected");
            }
        });
        stage.setOnCloseRequest(windowEvent -> button.getStyleClass().remove("custom-button-selected"));
        button.getStyleClass().add("custom-button-selected");
    }

    public void unselectedButton(Button principal){
        principal.getStyleClass().remove("custom-button-selected");
    }

    public void selectedButtonPane(Button button, Button beforeButton){
        if (beforeButton != null){
            beforeButton.getStyleClass().remove("custom-button-report-selected");
        }
        button.getStyleClass().add("custom-button-report-selected");
    }
}