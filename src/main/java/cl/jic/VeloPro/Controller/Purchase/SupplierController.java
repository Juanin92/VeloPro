package cl.jic.VeloPro.Controller.Purchase;

import cl.jic.VeloPro.Model.Entity.Purchase.Supplier;
import cl.jic.VeloPro.Service.Purchase.Interfaces.ISupplierService;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class SupplierController implements Initializable {

    @FXML private Button btnAddSupplier, btnCancel, btnSaveSupplier, btnUpdateSupplier, btnUpdate;
    @FXML private TableColumn<Supplier, Long> colId;
    @FXML private TableColumn<Supplier, String> colName;
    @FXML private TableColumn<Supplier, String> colRut;
    @FXML private TableView<Supplier> supplierTable;
    @FXML private CustomTextField txtEmail, txtName, txtPhone, txtRut;
    @FXML private StackPane stackPaneAddSupplier;
    @FXML private Label lblEmail, lblName, lblPhone, lblRut;

    @Autowired private ISupplierService supplierService;
    @Autowired private IRecordService recordService;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private NotificationManager notificationManager;
    @Autowired private Session session;

    private Supplier selectedSupplier;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graphicsValidator.handleTextfieldChangeWithLabel(txtName,lblName);
        graphicsValidator.handleTextfieldChangeWithLabel(txtRut,lblRut);
        graphicsValidator.handleTextfieldChangeWithLabel(txtPhone,lblPhone);
        graphicsValidator.handleTextfieldChangeWithLabel(txtEmail,lblEmail);
        clearField();
        loadDataStockList();
    }

    @FXML
    private void handleButtonSupplier(ActionEvent event){
        if (event.getSource().equals(btnAddSupplier)){
            stackPaneAddSupplier.setVisible(true);
            btnUpdateSupplier.setVisible(false);
            btnSaveSupplier.setVisible(true);

        } else if (event.getSource().equals(btnCancel)) {
            stackPaneAddSupplier.setVisible(false);
            btnUpdateSupplier.setVisible(false);
            clearField();

        } else if (event.getSource().equals(btnUpdateSupplier)) {
            stackPaneAddSupplier.setVisible(true);
            btnSaveSupplier.setVisible(false);
            btnUpdate.setVisible(true);

        } else if (event.getSource().equals(btnUpdate)) {
            updateSupplier(selectedSupplier);
            loadDataStockList();
        }
    }

    @FXML
    public void addSupplier(){
        try {
            Supplier supplier = new Supplier();
            supplier.setName(txtName.getText());
            supplier.setRut(txtRut.getText());
            supplier.setEmail(txtEmail.getText());
            supplier.setPhone(txtPhone.getText());
            supplierService.save(supplier);
            loadDataStockList();
            clearField();
            stackPaneAddSupplier.setVisible(false);
            notificationManager.successNotification("Registro Exitoso!", supplier.getName() + " ha sido agregado al sistema correctamente.", Pos.CENTER);
            recordService.registerAction(session.getCurrentUser(), "CREATE", "Crear Proveedor" + supplier.getName());
        }catch (Exception e){
            if (e.getMessage().equals("Proveedor Existente: Hay registro de este proveedor.")){
                notificationManager.errorNotification("Error!", e.getMessage(), Pos.CENTER);
            }
           handleValidationException(e.getMessage());
        }
    }

    @FXML
    private void updateSupplier(Supplier supplier){
        try {
            supplier.setName(txtName.getText());
            supplier.setRut(txtRut.getText());
            supplier.setPhone(txtPhone.getText());
            if (supplier.getEmail() == null){
                supplier.setEmail(null);
            }else {
                supplier.setEmail(txtEmail.getText());
            }
            supplierService.save(supplier);
            loadDataStockList();
            clearField();
            stackPaneAddSupplier.setVisible(false);
            notificationManager.successNotification("Actualización Exitoso!", supplier.getName() + " ha sido actualizado correctamente.", Pos.CENTER);
            recordService.registerAction(session.getCurrentUser(), "CHANGE", "Cambio Proveedor" + supplier.getName());
        }catch (Exception e){
            if (e.getMessage().equals("Proveedor Existente: Hay registro de este proveedor.")){
                notificationManager.errorNotification("Error!", e.getMessage(), Pos.CENTER);
            }
            handleValidationException(e.getMessage());
        }
    }

    public void loadDataStockList(){
        ObservableList<Supplier> supplierList = FXCollections.observableArrayList(supplierService.getAll());
        supplierTable.setItems(supplierList);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colRut.setCellValueFactory(new PropertyValueFactory<>("rut"));

        supplierTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                selectedSupplier = newValue;
                btnUpdateSupplier.setVisible(true);
                btnAddSupplier.setVisible(false);
                txtName.setText(selectedSupplier.getName());
                txtRut.setText(selectedSupplier.getRut());
                txtPhone.setText(selectedSupplier.getPhone());
                txtEmail.setText(selectedSupplier.getEmail());
            }else {
                btnUpdateSupplier.setVisible(false);
            }
        });
    }

    private void handleValidationException(String errorMessage){
        switch (errorMessage){
            case "Ingrese nombre válido.":
                graphicsValidator.settingAndValidationTextField(txtName, true, errorMessage);
                break;
            case "El rut no es correcto.":
                graphicsValidator.settingAndValidationTextField(txtRut, true, errorMessage);
                break;
            case "Ingrese número válido, Ej: +569 12345678":
                graphicsValidator.settingAndValidationTextField(txtPhone, true, errorMessage);
                break;
            case "Ingrese Email válido.":
                graphicsValidator.settingAndValidationTextField(txtEmail, true, errorMessage);
                break;
        }
    }

    private void clearField(){
        txtEmail.clear();
        txtPhone.setText("+569 ");
        txtName.clear();
        txtRut.clear();
    }
}