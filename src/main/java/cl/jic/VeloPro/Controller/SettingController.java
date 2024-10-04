package cl.jic.VeloPro.Controller;

import cl.jic.VeloPro.Controller.User.UserController;
import cl.jic.VeloPro.Model.Entity.Sale.CashRegister;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Service.Sale.Interfaces.ICashRegisterService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.ButtonManager;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import cl.jic.VeloPro.VeloProApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.Setter;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class SettingController implements Initializable {

    @FXML private Button btnAddUser, btnListCheckout, btnListUser, btnUpdateUser;
    @FXML private AnchorPane paneListCheckout;
    @FXML private StackPane paneSettingView;
    @FXML private TableView<CashRegister> cashRegisterTable;
    @FXML private TableColumn<CashRegister, Integer> colClosingAmount;
    @FXML private TableColumn<CashRegister, String> colComment;
    @FXML private TableColumn<CashRegister, LocalDateTime> colDateClosing;
    @FXML private TableColumn<CashRegister, LocalDateTime> colDateOpening;
    @FXML private TableColumn<CashRegister, Integer> colOpeningAmount;
    @FXML private TableColumn<CashRegister, Integer> colPosAmount;
    @FXML private TableColumn<CashRegister, String> colStatus;
    @FXML private TableColumn<CashRegister, User> colUser;

    @Autowired private ICashRegisterService cashRegisterService;
    @Autowired private IRecordService recordService;
    @Autowired private Session session;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private ButtonManager buttonManager;

    @Setter HomeController homeController;
    @Setter boolean activeToken;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private ObservableList<CashRegister> list = FXCollections.observableArrayList();
    private Button buttonSelected;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        managedUserView(currentUser);
        loadDataCashRegisterList();
        validateToken(activeToken);
        recordService.registerAction(currentUser, "VIEW", "Registro Caja");
    }

    @FXML
    private void handleButton(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnAddUser)){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/UserView/userView.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();
            paneSettingView.getChildren().clear();
            paneSettingView.getChildren().add(root);
            UserController userController = fxmlLoader.getController();
            userController.activeUserView();
            paneListCheckout.setVisible(false);
            buttonManager.selectedButtonPane(btnAddUser,buttonSelected);
            buttonSelected = btnAddUser;
        } else if (event.getSource().equals(btnListUser)) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/UserView/userView.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();
            paneSettingView.getChildren().clear();
            paneSettingView.getChildren().add(root);
            UserController userController = fxmlLoader.getController();
            userController.activeListUser();
            paneListCheckout.setVisible(false);
            buttonManager.selectedButtonPane(btnListUser,buttonSelected);
            buttonSelected = btnListUser;
        } else if (event.getSource().equals(btnListCheckout)) {
            paneSettingView.getChildren().clear();
            paneSettingView.getChildren().add(paneListCheckout);
            paneListCheckout.setVisible(true);
            buttonManager.selectedButtonPane(btnListCheckout,buttonSelected);
            buttonSelected = btnListCheckout;
        } else if (event.getSource().equals(btnUpdateUser) || activeToken) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/UserView/userView.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();
            paneSettingView.getChildren().clear();
            paneSettingView.getChildren().add(root);
            UserController userController = fxmlLoader.getController();
            userController.activeUserUpdateView();
            userController.loadInfoUser();
            userController.setActiveToken(activeToken);
            paneListCheckout.setVisible(false);
            buttonManager.selectedButtonPane(btnUpdateUser,buttonSelected);
            buttonSelected = btnUpdateUser;
        }
        homeController.handleButton(event);
    }

    private void validateToken(boolean active){
        if (active){
            btnListUser.setDisable(true);
            btnAddUser.setDisable(true);
            btnListCheckout.setDisable(true);
        }
    }

    public void loadDataCashRegisterList() {
        list = FXCollections.observableArrayList(cashRegisterService.getAll());
        cashRegisterTable.setItems(list);

        colDateOpening.setCellValueFactory(new PropertyValueFactory<>("dateOpening"));
        colDateClosing.setCellValueFactory(new PropertyValueFactory<>("dateClosing"));
        colOpeningAmount.setCellValueFactory(new PropertyValueFactory<>("amountOpening"));
        colClosingAmount.setCellValueFactory(new PropertyValueFactory<>("amountClosingCash"));
        colPosAmount.setCellValueFactory(new PropertyValueFactory<>("amountClosingPos"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("user"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        configurationTableView();
    }

    private void configurationTableView(){
        colOpeningAmount.setCellFactory(column -> {
            CustomTextField textField = new CustomTextField();
            return new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(currencyFormat.format(item));
                        autosize();
                        setOnMouseClicked(mouseEvent -> {
                            if (!isEmpty()) {
                                if (currentUser.getRole().equals(Rol.MASTER)){
                                    startEdit();
                                }
                            }
                        });
                    }
                }

                @Override
                public void startEdit() {
                    super.startEdit();
                    if (!isEmpty()) {
                        setText(null);
                        setGraphic(textField);
                        textField.setText(getItem() != 0 ? getItem().toString() : "");
                        textField.setOnKeyPressed(keyEvent -> {
                            if (keyEvent.getCode() == KeyCode.ENTER) {
                                commitEdit(Integer.parseInt(textField.getText()));
                                cashRegisterTable.refresh();
                                loadDataCashRegisterList();
                            }
                        });
                        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                            if (!newVal) {
                                commitEdit(Integer.parseInt(textField.getText()));
                                cashRegisterTable.refresh();
                                loadDataCashRegisterList();
                            }
                        });
                        getGraphic().requestFocus();
                    }
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setText(getString());
                    setGraphic(null);
                }

                @Override
                public void commitEdit(Integer newValue) {
                    super.commitEdit(newValue);
                    int index = getTableRow().getIndex();
                    if (newValue > 0) {
                        list = getTableView().getItems();
                        list.get(index).setAmountOpening(newValue);
                        cashRegisterService.updateRegister(list.get(index).getId(), null, newValue, null, null);
                    } else {
                        graphicsValidator.settingAndValidationTextField(textField, true, "");
                        cancelEdit();
                    }
                    setText(currencyFormat.format(newValue));
                    setGraphic(null);
                }

                private String getString() {
                    return getItem() == null ? "" : currencyFormat.format(getItem());
                }
            };
        });
        colClosingAmount.setCellFactory(column -> {
            CustomTextField textField = new CustomTextField();
            return new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(currencyFormat.format(item));
                        autosize();
                        setOnMouseClicked(mouseEvent -> {
                            if (!isEmpty()) {
                                if (currentUser.getRole().equals(Rol.MASTER)){
                                    startEdit();
                                }
                            }
                        });
                    }
                }

                @Override
                public void startEdit() {
                    super.startEdit();
                    if (!isEmpty()) {
                        setText(null);
                        setGraphic(textField);
                        textField.setText(getItem() != 0 ? getItem().toString() : "");
                        textField.setOnKeyPressed(keyEvent -> {
                            if (keyEvent.getCode() == KeyCode.ENTER) {
                                commitEdit(Integer.parseInt(textField.getText()));
                                cashRegisterTable.refresh();
                                loadDataCashRegisterList();
                            }
                        });
                        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                            if (!newVal) {
                                commitEdit(Integer.parseInt(textField.getText()));
                                cashRegisterTable.refresh();
                                loadDataCashRegisterList();
                            }
                        });
                        getGraphic().requestFocus();
                    }
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setText(getString());
                    setGraphic(null);
                }

                @Override
                public void commitEdit(Integer newValue) {
                    super.commitEdit(newValue);
                    int index = getTableRow().getIndex();
                    if (newValue > 0) {
                        list = getTableView().getItems();
                        list.get(index).setAmountClosingCash(newValue);
                        if (list.get(index).getAmountOpening() < list.get(index).getAmountClosingCash()) {
                            cashRegisterService.updateRegister(list.get(index).getId(), "CLOSED", null, newValue, null);
                        }
                    } else {
                        graphicsValidator.settingAndValidationTextField(textField, true, "");
                        cancelEdit();
                    }
                    setText(currencyFormat.format(newValue));
                    setGraphic(null);
                }

                private String getString() {
                    return getItem() == null ? "" : currencyFormat.format(getItem());
                }
            };
        });
        colPosAmount.setCellFactory(column -> {
            CustomTextField textField = new CustomTextField();
            return new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(currencyFormat.format(item));
                        autosize();
                        setOnMouseClicked(mouseEvent -> {
                            if (!isEmpty()) {
                                if (currentUser.getRole().equals(Rol.MASTER)){
                                    startEdit();
                                }
                            }
                        });
                    }
                }

                @Override
                public void startEdit() {
                    super.startEdit();
                    if (!isEmpty()) {
                        setText(null);
                        setGraphic(textField);
                        textField.setText(getItem() != 0 ? getItem().toString() : "");
                        textField.setOnKeyPressed(keyEvent -> {
                            if (keyEvent.getCode() == KeyCode.ENTER) {
                                commitEdit(Integer.parseInt(textField.getText()));
                                cashRegisterTable.refresh();
                                loadDataCashRegisterList();
                            }
                        });
                        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                            if (!newVal) {
                                commitEdit(Integer.parseInt(textField.getText()));
                                cashRegisterTable.refresh();
                                loadDataCashRegisterList();
                            }
                        });
                        getGraphic().requestFocus();
                    }
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setText(getString());
                    setGraphic(null);
                }

                @Override
                public void commitEdit(Integer newValue) {
                    super.commitEdit(newValue);
                    int index = getTableRow().getIndex();
                    if (newValue > 0) {
                        list = getTableView().getItems();
                        list.get(index).setAmountClosingPos(newValue);
                        cashRegisterService.updateRegister(list.get(index).getId(), null, null, null, newValue);
                    } else {
                        graphicsValidator.settingAndValidationTextField(textField, true, "");
                        cancelEdit();
                    }
                    setText(currencyFormat.format(newValue));
                    setGraphic(null);
                }

                private String getString() {
                    return getItem() == null ? "" : currencyFormat.format(getItem());
                }
            };
        });
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if (item.equals("OPEN")) {
                        setText("Abierto");
                    } else if (item.equals("CLOSED")) {
                        setText("Cerrado");
                    }
                    autosize();
                }
            }
        });
        colUser.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " " + item.getSurname());
                    autosize();
                }
            }
        });
        colDateOpening.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                    autosize();
                }
            }
        });
        colDateClosing.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                    autosize();
                }
            }
        });
    }

   private void managedUserView(User user){
        if(user.getRole().equals(Rol.SELLER)){
            btnAddUser.setDisable(true);
            btnListCheckout.setDisable(true);
            btnListUser.setDisable(true);
        }
    }
}