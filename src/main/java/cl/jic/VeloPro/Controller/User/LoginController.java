package cl.jic.VeloPro.Controller.User;

import cl.jic.VeloPro.Controller.HomeController;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Service.User.IUserService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.ButtonManager;
import cl.jic.VeloPro.Utility.EmailService;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import cl.jic.VeloPro.VeloProApplication;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class LoginController implements Initializable {

    @FXML private Button btnCancelLogin, btnLogin;
    @FXML private CustomTextField txtUsername, txtUserPassVisible;
    @FXML private CustomPasswordField txtUserPass;
    @FXML private Label lblUsername, lblPass, lblMessage;
    @FXML private ProgressBar progressBar;

    @Autowired private IUserService userService;
    @Autowired private IRecordService recordService;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private NotificationManager notificationManager;
    @Autowired private Session session;
    @Autowired private EmailService emailService;
    @Autowired private ButtonManager buttonManager;
    private boolean activeToken = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (progressBar != null){
            startProgressBar();
        }
        if (url.toString().contains("login.fxml")){
            graphicsValidator.handleTextfieldChangeWithLabel(txtUsername, lblUsername);
            graphicsValidator.handlePasswordFieldChange(txtUserPass, lblPass);
            graphicsValidator.handleTextfieldChangeWithLabel(txtUserPassVisible, lblPass);
            setupTogglePasswordVisibility(txtUserPass, txtUserPassVisible);
        }
    }

    @FXML private void handleButton(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnCancelLogin)){
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }else if (event.getSource().equals(btnLogin)){
            User authenticatedUser;
            String password = txtUserPass.isVisible() ? txtUserPass.getText() : txtUserPassVisible.getText();
            if (activeToken){
                authenticatedUser = userService.getAuthUserToken(txtUsername.getText(),password);
            }else {
                authenticatedUser = userService.getAuthUser(txtUsername.getText(),password);
            }
            if(authenticatedUser != null){
                session.setCurrentUser(authenticatedUser);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/home.fxml"));
                fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
                Parent root = fxmlLoader.load();
                HomeController homeController = fxmlLoader.getController();
                homeController.setActiveToken(activeToken);

                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Home - Principal");
                stage.setWidth(Screen.getPrimary().getBounds().getWidth());
                stage.setHeight(Screen.getPrimary().getBounds().getHeight());
                stage.show();
                Stage loginView = (Stage) ((Node) event.getSource()).getScene().getWindow();
                loginView.close();
                recordService.registerEntry(authenticatedUser);
            }else {
                lblMessage.setVisible(true);
            }
        }
    }

    @FXML public void forgetPassword(){
        try{
            if (txtUsername.getText().isEmpty()){
                graphicsValidator.settingAndValidationTextField(txtUsername, true, "Debe ingresar un nombre de usuario");
            } else {
                User user = userService.getUser(txtUsername.getText());
                userService.sendEmailCode(user);
                notificationManager.successNotification("Correo enviado", "Email con su código de verificación ha sido enviado", Pos.CENTER);
                activeToken = true;
                recordService.registerAction(user, "CHANGE PASSWORD", "Correo enviado con token");
            }
        }catch (Exception ex){
            notificationManager.errorNotification("Error!", ex.getMessage(), Pos.TOP_CENTER);
        }
    }

    private void setupTogglePasswordVisibility(CustomPasswordField passwordField, CustomTextField passwordVisibleField) {
        Button toggleVisibility = buttonManager.createButton("iconVisibility.png", "transparent", 15, 15);
        passwordVisibleField.setVisible(false);
        HBox parentHBox = (HBox) passwordField.getParent();
        parentHBox.getChildren().add(toggleVisibility);
        toggleVisibility.setOnAction(event -> {
            if (passwordVisibleField.isVisible()) {
                passwordVisibleField.setVisible(false);
                passwordField.setVisible(true);
                passwordField.setText(passwordVisibleField.getText());
            } else {
                passwordField.setVisible(false);
                passwordVisibleField.setVisible(true);
                passwordVisibleField.setText(passwordField.getText());
                passwordVisibleField.requestFocus();
            }
        });
    }

    private void startProgressBar() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(3), new KeyValue(progressBar.progressProperty(), 1))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }
}