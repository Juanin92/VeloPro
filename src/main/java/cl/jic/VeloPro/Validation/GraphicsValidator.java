package cl.jic.VeloPro.Validation;

import javafx.geometry.Point2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.stereotype.Component;

@Component
public class GraphicsValidator {

    public void settingAndValidationTextField(CustomTextField customTextField, boolean hasError, String exceptionMessage) {
        String borderColor = hasError ? "red" : (!customTextField.getText().trim().isEmpty() ? "#00ff00" : "transparent");
        customTextField.setStyle("-fx-border-color:  transparent transparent " + borderColor + "; -fx-background-color: transparent; -fx-text-fill: white;");
        if (hasError) {
            showErrorPopupTextField(customTextField, exceptionMessage, 36.0);
        } else {
            customTextField.setRight(null);
            customTextField.setStyle("-fx-border-color:  transparent transparent white; -fx-background-color: transparent; -fx-text-fill: white;");
        }
    }

    public void settingAndValidationFieldPassword(CustomPasswordField customPasswordField, boolean hasError, String exceptionMessage) {
        String borderColor = hasError ? "red" : (!customPasswordField.getText().trim().isEmpty() ? "#00ff00" : "transparent");
        customPasswordField.setStyle("-fx-border-color:  transparent transparent " + borderColor + "; -fx-background-color: transparent; -fx-text-fill: white;");
        if (hasError) {
            showErrorPopupPasswordField(customPasswordField,exceptionMessage);
        } else {
            customPasswordField.setRight(null);
            customPasswordField.setStyle("-fx-border-color:  transparent transparent white; -fx-background-color: transparent; -fx-text-fill: white;");
        }
    }

    public void settingAndValidationComboBox(ComboBox<?> comboBox, boolean hasError, String exceptionMessage) {
        String borderColor = hasError ? "red" : (!comboBox.getItems().isEmpty() ? "#00ff00" : "transparent");
        comboBox.setStyle("-fx-border-color: " + borderColor + "; -fx-border-radius: 10; -fx-background-radius: 10;");
        if (hasError) {
            Popup popup = new Popup();
            popup.setAutoHide(true);
            Label label = new Label(exceptionMessage);
            label.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-effect: dropshadow( one-pass-box , red , 2 , 0.0 , 0 , 0 );");
            popup.getContent().add(label);
            Stage primaryStage = (Stage) comboBox.getScene().getWindow();
            Point2D anchorPoint = comboBox.localToScreen(comboBox.getWidth(), 30.0);
            popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT);
            popup.show(primaryStage, anchorPoint.getX(), anchorPoint.getY());
        } else {
            comboBox.setStyle("-fx-border-color: transparent; -fx-border-radius: 10; -fx-background-radius: 10;");
        }
    }

    public void settingAndValidationFieldSale(CustomTextField customTextField, boolean hasError, String exceptionMessage) {
        String borderColor = hasError ? "red" : (!customTextField.getText().trim().isEmpty() ? "#00ff00" : "transparent");
        customTextField.setStyle("-fx-border-color: " + borderColor + ";" + "-fx-background-radius: 10;" + "-fx-border-radius: 10;");
        if (hasError) {
            showErrorPopupTextField(customTextField, exceptionMessage, 40.0);
        } else {
            customTextField.setRight(null);
        }
    }

    public void settingAndValidationDatePurchase(DatePicker date, boolean hasError, String exceptionMessage) {
        String borderColor = hasError ? "red" : "transparent";
        date.setStyle("-fx-border-color: " + borderColor + "; -fx-background-radius: 5;");
        if (hasError) {
            Popup popup = new Popup();
            popup.setAutoHide(true);
            Label label = new Label(exceptionMessage);
            label.setStyle("-fx-text-fill: red; -fx-font-size: 13px; -fx-background-radius: 5;");
            popup.getContent().add(label);
            Stage primaryStage = (Stage) date.getScene().getWindow();
            Point2D anchorPoint = date.localToScreen(date.getWidth(), 30.0);
            popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT);
            popup.show(primaryStage, anchorPoint.getX(), anchorPoint.getY());
        } else {
            date.setStyle("-fx-border-color:  transparent; -fx-background-radius: 5;");
        }
    }

    public void handleTextfieldChangeWithLabel(CustomTextField txtField, Label label){
        txtField.focusedProperty().addListener((observable,oldValue, newValue) -> label.setVisible(newValue));
        txtField.textProperty().addListener((observable, oldValue, newValue) -> settingAndValidationTextField(txtField, false, ""));
    }

    public void handleTextfieldChange(CustomTextField txtField){
        txtField.textProperty().addListener((observable, oldValue, newValue) -> settingAndValidationFieldSale(txtField, false, ""));
    }

    public void handlePasswordFieldChange(CustomPasswordField passwordField, Label label) {
        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> label.setVisible(newValue));
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> settingAndValidationFieldPassword(passwordField, false, ""));
    }

    public void handleComboBoxChange(ComboBox<?> comboBox) {
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> settingAndValidationComboBox(comboBox, false, ""));
    }

    private void showErrorPopupTextField(CustomTextField txt, String exceptionMessage, double width){
        Image icon = new Image("Images/Icons/iconError.png");
        ImageView error = new ImageView(icon);
        error.setFitWidth(16);
        error.setFitHeight(16);
        txt.setRight(error);
        Popup popup = new Popup();
        popup.setAutoHide(true);
        Label label = new Label(exceptionMessage);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-effect: dropshadow( one-pass-box , red , 2 , 0.0 , 0 , 0 );");
        popup.getContent().add(label);
        Stage primaryStage = (Stage) txt.getScene().getWindow();
        Point2D anchorPoint = txt.localToScreen(txt.getWidth(), width);
        popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT);
        popup.show(primaryStage, anchorPoint.getX(), anchorPoint.getY());
    }

    private void showErrorPopupPasswordField(CustomPasswordField txt, String exceptionMessage){
        Image icon = new Image("Images/Icons/iconError.png");
        ImageView error = new ImageView(icon);
        error.setFitWidth(16);
        error.setFitHeight(16);
        txt.setRight(error);
        Popup popup = new Popup();
        popup.setAutoHide(true);
        Label label = new Label(exceptionMessage);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-effect: dropshadow( one-pass-box , red , 2 , 0.0 , 0 , 0 );");
        popup.getContent().add(label);
        Stage primaryStage = (Stage) txt.getScene().getWindow();
        Point2D anchorPoint = txt.localToScreen(txt.getWidth(), 36.0);
        popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT);
        popup.show(primaryStage, anchorPoint.getX(), anchorPoint.getY());
    }
}