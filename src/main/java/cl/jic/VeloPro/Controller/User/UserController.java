package cl.jic.VeloPro.Controller.User;

import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Record.IRecordService;
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

    @FXML private CustomTextField txtEmail, txtName, txtRut, txtSurname, txtUsername;
    @FXML private CustomTextField txtEmailUpdate, txtNameUpdate, txtRutUpdate, txtSurnameUpdate, txtUsernameUpdate;
    @FXML private CustomTextField txtPassVisible, txtCurrentPasswordVisible, txtNewPasswordVisible;
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
    @FXML private HBox paneAddUserPass, paneNewPass, paneCurrentPass;

    @Autowired private IUserService userService;
    @Autowired private IRecordService recordService;
    @Autowired private ButtonManager buttonManager;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private NotificationManager notificationManager;
    @Autowired private Session session;
    @Setter private boolean activeToken;
    private User currentUser;
    private User userSelectedList;
    private boolean statusPanePass = false;
    private String password;

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
            password = txtPass.isVisible() ? txtPass.getText() : txtPassVisible.getText();
            user.setPassword(password);
            user.setRole(cbRol.getValue());
            userService.addUser(user);
            notificationManager.successNotification("Registro Exitoso!", "Usuario " + user.getName() + " " + user.getSurname() + ", Registrado en el sistema", Pos.CENTER);
            recordService.registerAction(currentUser, "CREATE", "Crea Usuario " + user.getRut());
        } catch (Exception e) {
            if (e.getMessage().equals("Usuario Existente: Ya hay existe el usuario")){
                notificationManager.errorNotification("Error", e.getMessage(), Pos.CENTER);
            }
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
            recordService.registerAction(currentUser, "CHANGE", "Cambio Usuario " + currentUser.getRut());
            tableUser.refresh();
            activeToken = false;
        }catch (Exception e){
            handleValidationException(e.getMessage());
        }
    }

    private void changePassword(){
        try{
            String currentPassword = txtCurrentPassword.isVisible() ? txtCurrentPassword.getText() : txtCurrentPasswordVisible.getText();
            if (currentPassword.equals(currentUser.getPassword())){
                password = txtNewPassword.isVisible() ? txtNewPassword.getText() : txtNewPasswordVisible.getText();
                currentUser.setPassword(password);
            }else {
                btnSendPass.setVisible(true);
                btnSendPass.setOnAction(event -> {
                    try{
                        userService.sendEmailCode(currentUser);
                        notificationManager.successNotification("Envio Exitoso", "Se ha enviado a su correo el código, Ingrese en el campo contraseña actual", Pos.CENTER);
                        recordService.registerAction(currentUser, "CHANGE", "Cambio Password " + currentUser.getRut());
                    }catch (Exception e){
                        notificationManager.errorNotification("Error de Envio", e.getMessage(), Pos.CENTER);
                    }
                });
                if (currentPassword.equals(currentUser.getPassword())){
                    password = txtNewPassword.isVisible() ? txtNewPassword.getText() : txtNewPasswordVisible.getText();
                    if (password.isEmpty() || password.length() >= 7){
                        currentUser.setPassword(password);
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
                paneCurrentPass.setVisible(true);
                paneNewPass.setVisible(true);
            } else {
                paneChangePassword.setVisible(false);
                paneCurrentPass.setVisible(false);
                paneNewPass.setVisible(false);
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
                            recordService.registerAction(currentUser, "DELETE", "Eliminar Usuario " + user.getRut());
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
        txtPassVisible.clear();
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
        graphicsValidator.handleTextfieldChangeWithLabel(txtPassVisible, lblPass);
        graphicsValidator.handlePasswordFieldChange(txtPass, lblPass);
        graphicsValidator.handleTextfieldChangeWithLabel(txtCurrentPasswordVisible, lblCurrentPassword);
        graphicsValidator.handlePasswordFieldChange(txtCurrentPassword, lblCurrentPassword);
        graphicsValidator.handleTextfieldChangeWithLabel(txtNewPasswordVisible, lblNewPassword);
        graphicsValidator.handlePasswordFieldChange(txtNewPassword, lblNewPassword);

        for (Rol rol : Rol.values()) {
            cbRol.getItems().add(rol);
        }
        setupTogglePasswordVisibility(txtPass, txtPassVisible);
        setupTogglePasswordVisibility(txtCurrentPassword, txtCurrentPasswordVisible);
        setupTogglePasswordVisibility(txtNewPassword, txtNewPasswordVisible);
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

    private void showAndLoadaElementsTable(){
        if (currentUser.getRole().equals(Rol.MASTER)){
            paneUpdateList.setVisible(true);
            txtNameUpdate.setText(userSelectedList.getName());
            txtSurnameUpdate.setText(userSelectedList.getSurname());
            txtRutUpdate.setText(userSelectedList.getRut());
            txtEmailUpdate.setText(userSelectedList.getEmail());
            txtUsernameUpdate.setText(userSelectedList.getUsername());
            txtPassUpdate.setText(userSelectedList.getPassword());
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
                    recordService.registerAction(currentUser, "CHANGE", "Cambio para usuario: " + userSelectedList.getName());
                }catch (Exception e){
                    handleValidationExceptionList(e.getMessage());
                }
            });
        }
        if (!userSelectedList.isStatus()){
            btnActivate.setVisible(true);
            btnActivate.setOnAction(event -> userService.activateUser(userSelectedList));
            recordService.registerAction(currentUser, "CHANGE", "Activar usurario: " + userSelectedList.getRut());
        }else {
            btnActivate.setVisible(false);
        }
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
            case "Ingrese nombre de usuario.", "Este nombre de usuario ya existe":
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
        paneAddUserPass.setVisible(true);
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
        paneAddUserPass.setVisible(false);
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