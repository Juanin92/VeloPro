package cl.jic.VeloPro.Controller.Customer;

import cl.jic.VeloPro.Model.Entity.Customer.Customer;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Customer.Interface.ICustomerService;
import cl.jic.VeloPro.Service.Record.IRecordService;
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
public class AddEditCustomerController implements Initializable {

    @FXML private Button btnCancelCustomer, btnSaveCustomer, btnUpdateCustomer, btnActive;
    @FXML private CustomTextField txtEmailCustomer, txtNameCustomer, txtPhoneCustomer, txtSurnameCustomer;
    @FXML private Label lblEmail, lblName, lblPhone, lblSurname;
    @FXML private StackPane stackpane;

    @Autowired private ICustomerService customerService;
    @Autowired private ICustomerList ICustomerList;
    @Autowired private IRecordService recordService;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private Session session;
    @Autowired private NotificationManager notificationManager;
    @Autowired private ButtonManager buttonManager;
    @Setter private Customer currentCustomer;
    @Setter private Button principal;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        graphicsValidator.handleTextfieldChangeWithLabel(txtNameCustomer,lblName);
        graphicsValidator.handleTextfieldChangeWithLabel(txtSurnameCustomer,lblSurname);
        graphicsValidator.handleTextfieldChangeWithLabel(txtEmailCustomer,lblEmail);
        graphicsValidator.handleTextfieldChangeWithLabel(txtPhoneCustomer,lblPhone);
        Platform.runLater(this::infoCustomer);
    }

    @FXML
    private void handleButtonCustomer(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnCancelCustomer)) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            currentCustomer = null;
            if (principal != null){
                buttonManager.unselectedButton(principal);
            }
        } else if (event.getSource().equals(btnUpdateCustomer)) {
            updateCustomer(currentCustomer);
        }
    }

    @FXML
    public void addCustomer(){
        try{
            Customer customer = new Customer();
            customer.setName(txtNameCustomer.getText());
            customer.setSurname(txtSurnameCustomer.getText());
            customer.setPhone(txtPhoneCustomer.getText());
            customer.setEmail(txtEmailCustomer.getText());
            customerService.addNewCustomer(customer);
            notificationManager.successNotification("Registro Exitoso!", "Cliente " + customer.getName() + " " + customer.getSurname() + ", " + "Registrado en el sistema", Pos.CENTER);
            recordService.registerAction(currentUser,"CREATE", "Crear Cliente " + customer.getName() + " " + customer.getSurname());
            closeView();
        } catch (IllegalArgumentException e){
            if (e.getMessage().equals("Cliente Existente: Hay registro de este cliente.")){
                notificationManager.errorNotification("Error!", e.getMessage(), Pos.CENTER);
            }
            handleValidationException(e.getMessage());
        }
        ICustomerList.loadDataCustomerList();
    }

    private void updateCustomer(Customer customer){
        try{
            customer.setName(txtNameCustomer.getText());
            customer.setSurname(txtSurnameCustomer.getText());
            customer.setPhone(txtPhoneCustomer.getText());
            customer.setEmail(txtEmailCustomer.getText());
            customerService.updateCustomer(customer);
            notificationManager.successNotification("Actualizado Exitoso!", "Cliente " + customer.getName() + " " + customer.getSurname() + ", " + "Se ha Actualizado.", Pos.CENTER);
            recordService.registerAction(currentUser,"CHANGE", "Cambio Cliente " + customer.getName() + " " + customer.getSurname());
            closeView();
            currentCustomer = null;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Cliente Existente: Hay registro de este cliente.")){
                notificationManager.errorNotification("Error!", e.getMessage(), Pos.CENTER);
            }
            handleValidationException(e.getMessage());
        }
        ICustomerList.loadDataCustomerList();
    }

    private void infoCustomer(){
        if (currentCustomer != null) {
            txtNameCustomer.setDisable(currentUser.getRole().equals(Rol.SELLER));
            txtSurnameCustomer.setDisable(currentUser.getRole().equals(Rol.SELLER));
            if (currentCustomer.isAccount()) {
                txtNameCustomer.setText(currentCustomer.getName());
                txtSurnameCustomer.setText(currentCustomer.getSurname());
                txtPhoneCustomer.setText(currentCustomer.getPhone());
                txtEmailCustomer.setText(currentCustomer.getEmail());
                btnSaveCustomer.setVisible(false);
                btnUpdateCustomer.setVisible(true);
            } else {
                btnActive.setVisible(true);
                btnUpdateCustomer.setVisible(false);
                btnSaveCustomer.setVisible(false);
                btnActive.setOnAction(event -> {
                    currentCustomer.setAccount(true);
                    txtNameCustomer.setText(currentCustomer.getName());
                    txtSurnameCustomer.setText(currentCustomer.getSurname());
                    txtPhoneCustomer.setText(currentCustomer.getPhone());
                    txtEmailCustomer.setText(currentCustomer.getEmail());
                    btnSaveCustomer.setVisible(false);
                    btnUpdateCustomer.setVisible(true);
                    recordService.registerAction(currentUser,"ACTIVE", "Activo Cliente " + currentCustomer.getName() + " " + currentCustomer.getSurname());
                });
            }
        }
    }

    private void handleValidationException(String errorMessage){
        switch (errorMessage){
            case "Ingrese nombre válido.":
                graphicsValidator.settingAndValidationTextField(txtNameCustomer,true, errorMessage);
                break;
            case "Ingrese apellido válido.":
                graphicsValidator.settingAndValidationTextField(txtSurnameCustomer,true, errorMessage);
                break;
            case "Ingrese número válido, Ej: +569 12345678":
                graphicsValidator.settingAndValidationTextField(txtPhoneCustomer,true, errorMessage);
                break;
            case "Ingrese Email válido.":
                graphicsValidator.settingAndValidationTextField(txtEmailCustomer,true, errorMessage);
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