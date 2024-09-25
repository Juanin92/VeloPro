package cl.jic.VeloPro.Controller.User;

import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.User.IUserService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.ButtonManager;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.Setter;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
public class UserController implements Initializable {

    @FXML private CustomTextField txtEmail, txtName, txtRut, txtSurname, txtUsername, txtPassVisible;
    @FXML private CustomTextField txtEmailUpdate, txtNameUpdate, txtRutUpdate, txtSurnameUpdate, txtUsernameUpdate;
    @FXML private CustomPasswordField txtPass, txtPassUpdate, txtNewPassword, txtCurrentPassword;
    @FXML private Button btnAdd, btnUpdate, btnActivate, btnListUpdate, btnSendPass;
    @FXML private Label lblName, lblSurname, lblRut, lblEmail, lblPass, lblUsername, lblUpdate, lblAdd, lblCurrentPassword, lblNewPassword;
    @FXML private ComboBox<Rol> cbRol;
    @FXML private TableColumn<User, Void> colAction;
    @FXML private TableColumn<User, LocalDate> colDate;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, Rol> colRol;
    @FXML private TableColumn<User, String> colRut;
    @FXML private TableColumn<User, Boolean> colStatus;
    @FXML private TableColumn<User, String> colSurname;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableView<User> tableUser;
    @FXML private CheckBox cxChangePass;
    @FXML private AnchorPane paneAddUser, paneListUser, paneChangePassword, paneUpdateList;

    @Autowired private IUserService userService;
    @Autowired private ButtonManager buttonManager;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private NotificationManager notificationManager;
    @Autowired private Session session;
    @Setter private boolean activeToken;
    private User currentUser;
    private User userSelectedList;
    private boolean statusPanePass = false;
    private boolean passwordVisible = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        loadData();
        loadDataUserList();
        changePasswordVisibility();
    }

    @FXML
    private void addNewUser() {
        try {
            User user = new User();
            user.setName(txtName.getText());
            user.setSurname(txtSurname.getText());
            user.setRut(txtRut.getText());
            user.setEmail(txtEmail.getText());
            user.setUsername(txtUsername.getText());
            user.setPassword(txtPass.getText());
            user.setRole(cbRol.getValue());
            userService.addUser(user);
            notificationManager.successNotification("Registro Exitoso!", "Usuario " + user.getName() + " " + user.getSurname() + ", Registrado en el sistema", Pos.CENTER);
        } catch (Exception e) {
            handleValidationException(e.getMessage());
        }
    }

    @FXML
    private void updateUser(){
        try{
            if (statusPanePass){
               changePassword();
            }else{
                currentUser.setPassword(currentUser.getPassword());
            }
            currentUser.setEmail(txtEmail.getText());
            currentUser.setUsername(txtUsername.getText());
            userService.updateUser(currentUser);
            notificationManager.successNotification("Actualización Exitosa!", "Usuario " + currentUser.getName() + " " + currentUser.getSurname() + ", Actualizado en el sistema", Pos.CENTER);
            tableUser.refresh();
            activeToken = false;
        }catch (Exception e){
            handleValidationException(e.getMessage());
        }
    }

    private void changePassword(){
        try{
            if (txtCurrentPassword.getText().equals(currentUser.getPassword())){
                currentUser.setPassword(txtNewPassword.getText());
            }else {
                btnSendPass.setOnAction(event -> {
                    try{
                        userService.sendEmailCode(currentUser);
                        notificationManager.successNotification("Envio Exitoso", "Se ha enviado a su correo el código, Ingrese en el campo contraseña actual", Pos.CENTER);
                    }catch (Exception e){
                        notificationManager.errorNotification("Error de Envio", e.getMessage(), Pos.CENTER);
                    }
                });
                if (txtCurrentPassword.getText().equals(currentUser.getPassword())){
                    if (txtNewPassword.getText().isEmpty() || txtNewPassword.getText().length() >= 7){
                        currentUser.setPassword(txtNewPassword.getText());
                    }else {
                        graphicsValidator.settingAndValidationFieldPassword(txtNewPassword, true, "Ingrese contraseña válido. (Debe tener 8 o más caracteres o números)");
                    }
                }else {
                    graphicsValidator.settingAndValidationFieldPassword(txtCurrentPassword, true, "Código no es el enviado.");
                }
            }
        }catch (Exception e){
            graphicsValidator.settingAndValidationFieldPassword(txtCurrentPassword, true, "Contraseña no coincide con el actual registro, Envie un código a su email");
        }
    }

    private void changePasswordVisibility(){
        cxChangePass.selectedProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (newValue){
                statusPanePass = true;
                cxChangePass.setVisible(false);
                paneChangePassword.setVisible(true);
            } else {
                paneChangePassword.setVisible(false);
            }
        }));
    }

    public void loadInfoUser(){
        cbRol.setValue(currentUser.getRole());
        txtRut.setText(currentUser.getRut());
        txtName.setText(currentUser.getName());
        txtSurname.setText(currentUser.getSurname());
        txtEmail.setText(currentUser.getEmail());
        txtUsername.setText(currentUser.getUsername());
        txtPass.setText(currentUser.getPassword());
    }

    public void loadDataUserList() {
        ObservableList<User> list = FXCollections.observableArrayList(userService.getAllUser());
        tableUser.setItems(list);

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colRut.setCellValueFactory(new PropertyValueFactory<>("rut"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("role"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableUser.getSelectionModel().selectedItemProperty().addListener((observableValue, user, newValue) -> {
            userSelectedList = newValue;
            showAndLoadaElementsTable();
        });
        configurationTableView();
    }

    private void configurationTableView() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {

            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btnEliminate = buttonManager.createButton("btnDeleteIcon.png", "transparent", 30, 30);

                    {
                        btnEliminate.setOnAction((event) -> {
                            User user = getTableView().getItems().get(getIndex());
                            userService.deleteUser(user);
                            loadDataUserList();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            User user = getTableView().getItems().get(getIndex());
                            btnEliminate.setDisable(!user.isStatus());
                            HBox buttons = new HBox(btnEliminate);
                            buttons.setAlignment(Pos.CENTER);
                            buttons.setSpacing(5);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);

        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item ? "Activo" : "Inactivo");
                    autosize();
                }
            }
        });
    }

    @FXML
    private void cleanFields() {
        txtName.clear();
        txtSurname.clear();
        txtEmail.clear();
        txtRut.clear();
        txtPass.clear();
        txtUsername.clear();
        if (statusPanePass){
            cxChangePass.setVisible(true);
        }
        paneChangePassword.setVisible(false);
    }

    private void loadData() {
        graphicsValidator.handleComboBoxChange(cbRol);
        graphicsValidator.handleTextfieldChangeWithLabel(txtRut, lblRut);
        graphicsValidator.handleTextfieldChangeWithLabel(txtName, lblName);
        graphicsValidator.handleTextfieldChangeWithLabel(txtSurname, lblSurname);
        graphicsValidator.handleTextfieldChangeWithLabel(txtEmail, lblEmail);
        graphicsValidator.handleTextfieldChangeWithLabel(txtUsername, lblUsername);
        graphicsValidator.handlePasswordFieldChange(txtPass, lblPass);
        graphicsValidator.handlePasswordFieldChange(txtCurrentPassword, lblCurrentPassword);
        graphicsValidator.handlePasswordFieldChange(txtNewPassword, lblNewPassword);

        for (Rol rol : Rol.values()) {
            cbRol.getItems().add(rol);
        }

        Button toggleVisibility = buttonManager.createButton("iconVisibility.png","transparent", 15, 15);
        txtPass.setRight(toggleVisibility);
        toggleVisibility.setOnAction(event -> togglePasswordVisibility(toggleVisibility));
    }

    private void togglePasswordVisibility(Button toggleVisibility) {
        if (passwordVisible) {
            txtPassVisible.setVisible(false);
            txtPass.setRight(toggleVisibility);
            txtPass.setVisible(true);

            txtPass.setText(txtPassVisible.getText());
        } else {
            txtPassVisible.setVisible(true);
            txtPass.setVisible(false);
            txtPassVisible.setRight(toggleVisibility);
            txtPassVisible.setText(txtPass.getText());
        }
        passwordVisible = !passwordVisible;
    }

    private void showAndLoadaElementsTable(){
        paneUpdateList.setVisible(true);
        txtNameUpdate.setText(userSelectedList.getName());
        txtSurnameUpdate.setText(userSelectedList.getSurname());
        txtRutUpdate.setText(userSelectedList.getRut());
        txtEmailUpdate.setText(userSelectedList.getEmail());
        txtUsernameUpdate.setText(userSelectedList.getUsername());
        txtPassUpdate.setText(userSelectedList.getPassword());
        if (!userSelectedList.isStatus()){
            btnActivate.setOnAction(event -> userService.activateUser(userSelectedList));
        }else {
            btnActivate.setDisable(true);
        }
        btnListUpdate.setOnAction(event -> {
            try{
                if (txtPassUpdate.getText().equals(userSelectedList.getPassword())){
                    userSelectedList.setPassword(userSelectedList.getPassword());
                }else {
                    userSelectedList.setPassword(txtPassUpdate.getText());
                }
                userSelectedList.setName(txtNameUpdate.getText());
                userSelectedList.setSurname(txtSurnameUpdate.getText());
                userSelectedList.setRut(txtRutUpdate.getText());
                userSelectedList.setEmail(txtEmailUpdate.getText());
                userSelectedList.setUsername(txtUsernameUpdate.getText());
                userService.updateUser(userSelectedList);
                notificationManager.successNotification("Actualización Correcta!", "Actualización realizada correctamente", Pos.CENTER);
            }catch (Exception e){
                handleValidationExceptionList(e.getMessage());
            }
        });
        tableUser.refresh();
    }

    private void handleValidationException(String errorMessage) {
        switch (errorMessage) {
            case "Seleccione un rol para el usuario":
                graphicsValidator.settingAndValidationComboBox(cbRol, true, errorMessage);
                break;
            case "El rut no es correcto.":
                graphicsValidator.settingAndValidationTextField(txtRut, true, errorMessage);
                break;
            case "Ingrese nombre válido.":
                graphicsValidator.settingAndValidationTextField(txtName, true, errorMessage);
                break;
            case "Ingrese apellido válido.":
                graphicsValidator.settingAndValidationTextField(txtSurname, true, errorMessage);
                break;
            case "Ingrese Email válido.":
                graphicsValidator.settingAndValidationTextField(txtEmail, true, errorMessage);
                break;
            case "Ingrese nombre de usuario.":
                graphicsValidator.settingAndValidationTextField(txtUsername, true, errorMessage);
                break;
            case "Ingrese contraseña válido. (Debe tener 8 o más caracteres o números)":
                graphicsValidator.settingAndValidationFieldPassword(txtPass, true, errorMessage);
                break;
        }
    }

    private void handleValidationExceptionList(String errorMessage) {
        switch (errorMessage) {
            case "El rut no es correcto.":
                graphicsValidator.settingAndValidationTextField(txtRutUpdate, true, errorMessage);
                break;
            case "Ingrese nombre válido.":
                graphicsValidator.settingAndValidationTextField(txtNameUpdate, true, errorMessage);
                break;
            case "Ingrese apellido válido.":
                graphicsValidator.settingAndValidationTextField(txtSurnameUpdate, true, errorMessage);
                break;
            case "Ingrese Email válido.":
                graphicsValidator.settingAndValidationTextField(txtEmailUpdate, true, errorMessage);
                break;
            case "Ingrese nombre de usuario.":
                graphicsValidator.settingAndValidationTextField(txtUsernameUpdate, true, errorMessage);
                break;
            case "Ingrese contraseña válido. (Debe tener 8 o más caracteres o números)":
                graphicsValidator.settingAndValidationFieldPassword(txtPassUpdate, true, errorMessage);
                break;
        }
    }

    public void activeUserView() {
        paneListUser.setVisible(false);
        paneAddUser.setVisible(true);
        lblAdd.setVisible(true);
        lblUpdate.setVisible(false);
        cbRol.setDisable(false);
        txtRut.setDisable(false);
        txtName.setDisable(false);
        txtSurname.setDisable(false);
        txtPass.setVisible(true);
        cxChangePass.setVisible(false);
        btnAdd.setVisible(true);
        btnUpdate.setVisible(false);
    }

    public void activeListUser() {
        paneListUser.setVisible(true);
        paneAddUser.setVisible(false);
    }

    public void activeUserUpdateView() {
        paneListUser.setVisible(false);
        paneAddUser.setVisible(true);
        lblAdd.setVisible(false);
        lblUpdate.setVisible(true);
        cbRol.setDisable(true);
        txtRut.setDisable(true);
        txtName.setDisable(true);
        txtSurname.setDisable(true);
        txtPass.setVisible(false);
        cxChangePass.setVisible(true);
        btnAdd.setVisible(false);
        btnUpdate.setVisible(true);
    }
}