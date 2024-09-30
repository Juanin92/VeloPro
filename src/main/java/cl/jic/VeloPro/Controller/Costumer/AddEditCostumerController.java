package cl.jic.VeloPro.Controller.Costumer;

import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Costumer.Interface.ICostumerService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.ButtonManager;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Setter;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class AddEditCostumerController implements Initializable {

    @FXML private Button btnCancelCostumer, btnSaveCostumer, btnUpdateCostumer, btnActive;
    @FXML private CustomTextField txtEmailCostumer, txtNameCostumer, txtPhoneCostumer, txtSurnameCostumer;
    @FXML private Label lblEmail, lblName, lblPhone, lblSurname;
    @FXML private StackPane stackpane;

    @Autowired private ICostumerService costumerService;
    @Autowired private ICostumerList ICostumerList;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private Session session;
    @Autowired private NotificationManager notificationManager;
    @Autowired private ButtonManager buttonManager;
    @Setter private Costumer currentCostumer;
    @Setter private Button principal;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        graphicsValidator.handleTextfieldChangeWithLabel(txtNameCostumer,lblName);
        graphicsValidator.handleTextfieldChangeWithLabel(txtSurnameCostumer,lblSurname);
        graphicsValidator.handleTextfieldChangeWithLabel(txtEmailCostumer,lblEmail);
        graphicsValidator.handleTextfieldChangeWithLabel(txtPhoneCostumer,lblPhone);
        Platform.runLater(this::infoCostumer);
    }

    @FXML
    private void handleButtonCostumer(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnCancelCostumer)) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            currentCostumer = null;
            if (principal != null){
                buttonManager.unselectedButton(principal);
            }
        } else if (event.getSource().equals(btnUpdateCostumer)) {
            updateCostumer(currentCostumer);
        }
    }

    @FXML
    public void addCostumer(){
        try{
            Costumer costumer = new Costumer();
            costumer.setName(txtNameCostumer.getText());
            costumer.setSurname(txtSurnameCostumer.getText());
            costumer.setPhone(txtPhoneCostumer.getText());
            costumer.setEmail(txtEmailCostumer.getText());
            costumerService.addNewCostumer(costumer);
            notificationManager.successNotification("Registro Exitoso!", "Cliente " + costumer.getName() + " " + costumer.getSurname() + ", " + "Registrado en el sistema", Pos.CENTER);
            closeView();
        } catch (IllegalArgumentException e){
            if (e.getMessage().equals("Cliente Existente: Hay registro de este cliente.")){
                notificationManager.errorNotification("Error!", e.getMessage(), Pos.CENTER);
            }
            handleValidationException(e.getMessage());
        }
        ICostumerList.loadDataCostumerList();
    }

    private void updateCostumer(Costumer costumer){
        try{
            costumer.setName(txtNameCostumer.getText());
            costumer.setSurname(txtSurnameCostumer.getText());
            costumer.setPhone(txtPhoneCostumer.getText());
            costumer.setEmail(txtEmailCostumer.getText());
            costumerService.updateCostumer(costumer);
            notificationManager.successNotification("Actualizado Exitoso!", "Cliente " + costumer.getName() + " " + costumer.getSurname() + ", " + "Se ha Actualizado.", Pos.CENTER);
            closeView();
            currentCostumer = null;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Cliente Existente: Hay registro de este cliente.")){
                notificationManager.errorNotification("Error!", e.getMessage(), Pos.CENTER);
            }
            handleValidationException(e.getMessage());
        }
        ICostumerList.loadDataCostumerList();
    }

    private void infoCostumer(){
        if (currentCostumer != null) {
            txtNameCostumer.setDisable(currentUser.getRole().equals(Rol.SELLER));
            txtSurnameCostumer.setDisable(currentUser.getRole().equals(Rol.SELLER));
            if (currentCostumer.isAccount()) {
                txtNameCostumer.setText(currentCostumer.getName());
                txtSurnameCostumer.setText(currentCostumer.getSurname());
                txtPhoneCostumer.setText(currentCostumer.getPhone());
                txtEmailCostumer.setText(currentCostumer.getEmail());
                btnSaveCostumer.setVisible(false);
                btnUpdateCostumer.setVisible(true);
            } else {
                btnActive.setVisible(true);
                btnUpdateCostumer.setVisible(false);
                btnSaveCostumer.setVisible(false);
                btnActive.setOnAction(event -> {
                    currentCostumer.setAccount(true);
                    txtNameCostumer.setText(currentCostumer.getName());
                    txtSurnameCostumer.setText(currentCostumer.getSurname());
                    txtPhoneCostumer.setText(currentCostumer.getPhone());
                    txtEmailCostumer.setText(currentCostumer.getEmail());
                    btnSaveCostumer.setVisible(false);
                    btnUpdateCostumer.setVisible(true);
                });
            }
        }
    }

    private void handleValidationException(String errorMessage){
        switch (errorMessage){
            case "Ingrese nombre válido.":
                graphicsValidator.settingAndValidationTextField(txtNameCostumer,true, errorMessage);
                break;
            case "Ingrese apellido válido.":
                graphicsValidator.settingAndValidationTextField(txtSurnameCostumer,true, errorMessage);
                break;
            case "Ingrese número válido, Ej: +569 12345678":
                graphicsValidator.settingAndValidationTextField(txtPhoneCostumer,true, errorMessage);
                break;
            case "Ingrese Email válido.":
                graphicsValidator.settingAndValidationTextField(txtEmailCostumer,true, errorMessage);
                break;
        }
    }

    private void closeView(){
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(4), e -> {
            Stage stage = (Stage) stackpane.getScene().getWindow();
            stage.close();
        }));
        delay.play();
    }
}