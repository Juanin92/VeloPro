package cl.jic.VeloPro.Controller.Sale;

import cl.jic.VeloPro.Model.Entity.Sale.CashRegister;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Service.Sale.Interfaces.ICashRegisterService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.Setter;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class CashRegisterController implements Initializable {

    @FXML private Button btnRegisterOpening, btnRegisterClosing;
    @FXML private Label lblOpeningQuantity, lblClosingPOS, lblClosingQuantity;
    @FXML private CustomTextField txtOpeningQuantity, txtClosingPOS, txtClosingQuantity, txtComment;

    @Autowired private Session session;
    @Autowired private ICashRegisterService cashRegisterService;
    @Autowired private GraphicsValidator graphicsValidator;
    @Setter private Stage homeView;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        if (url.toString().contains("openingCashRegister.fxml")){
            handleTextfieldChange(txtOpeningQuantity, lblOpeningQuantity);
        }
        if (url.toString().contains("closingCashRegister.fxml")){
            handleTextfieldChange(txtClosingQuantity, lblClosingQuantity);
            handleTextfieldChange(txtClosingPOS, lblClosingPOS);
        }
    }

    @FXML
    public void openingRegister(){
        try{
            CashRegister cashRegister = new CashRegister();
            cashRegister.setAmountOpening(Integer.parseInt(txtOpeningQuantity.getText()));
            cashRegister.setUser(currentUser);
            cashRegisterService.addRegisterOpening(cashRegister);
            Stage stage = (Stage) btnRegisterOpening.getScene().getWindow();
            stage.close();
        }catch (NumberFormatException e) {
            handleValidationException("Debe ingresar un número válido", txtOpeningQuantity);
        } catch (IllegalArgumentException e) {
            handleValidationException(e.getMessage(), txtOpeningQuantity);
        }
    }

    @FXML
    public void closingRegister(){
        try{
            CashRegister cashRegister = cashRegisterService.getRegisterByUser(currentUser.getId());
            cashRegister.setAmountClosingCash(Integer.parseInt(txtClosingQuantity.getText()));
            cashRegister.setAmountClosingPos(Integer.parseInt(txtClosingPOS.getText()));
            try{
                cashRegisterService.addRegisterClosing(cashRegister);
                closeViewClosing();
            }catch (IllegalArgumentException exception){
                txtComment.setVisible(true);
                txtClosingQuantity.setVisible(false);
                txtClosingPOS.setVisible(false);
                btnRegisterClosing.setVisible(false);
                handleValidationException(exception.getMessage(), txtComment);
                txtComment.setOnAction(event -> {
                    closingRegisterComment(cashRegister);
                });
            }
        }catch (NumberFormatException e) {
            handleValidationException("Debe ingresar un número válido", txtClosingQuantity);
            handleValidationException("Debe ingresar un número válido", txtClosingPOS);
        }catch (Exception e){
            handleValidationException(e.getMessage(), txtClosingQuantity);
            handleValidationException(e.getMessage(), txtClosingPOS);
        }
    }

    private void closingRegisterComment(CashRegister cashRegister){
        try{
            cashRegister.setComment(txtComment.getText());
            cashRegisterService.addRegisterValidateComment(cashRegister);
            closeViewClosing();
        }catch (IllegalArgumentException ex){
            handleValidationException(ex.getMessage(), txtComment);
        }
    }

    private void closeViewClosing(){
        Stage stage = (Stage) btnRegisterClosing.getScene().getWindow();
        stage.close();
        if (homeView != null) {
            homeView.close();
        }
    }

    private void handleValidationException(String errorMessage,CustomTextField txtField){
        graphicsValidator.settingAndValidationTextField(txtField, true, errorMessage);
    }

    private void handleTextfieldChange(CustomTextField txtField, Label label) {
        txtField.focusedProperty().addListener((observable, oldValue, newValue) -> label.setVisible(newValue));
        txtField.textProperty().addListener((observable, oldValue, newValue) -> graphicsValidator.settingAndValidationTextField(txtField, false, ""));
    }
}