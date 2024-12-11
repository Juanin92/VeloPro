package cl.jic.VeloPro.Controller.Customer;

import cl.jic.VeloPro.Controller.HomeController;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.ButtonManager;
import cl.jic.VeloPro.Utility.EmailService;
import cl.jic.VeloPro.Model.Entity.Customer.Customer;
import cl.jic.VeloPro.Model.Enum.PaymentStatus;
import cl.jic.VeloPro.Service.Customer.Interface.ICustomerService;
import cl.jic.VeloPro.Service.Customer.Interface.ITicketHistoryService;
import cl.jic.VeloPro.Validation.ShowingStageValidation;
import cl.jic.VeloPro.VeloProApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

@Component
public class CustomerController implements Initializable, ICustomerList {

    @FXML private Button btnAddCustomer;
    @FXML private TextField txtFindCustomer;
    @FXML private TableColumn<Customer, Integer> colDebt;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, Boolean> colAccount;
    @FXML private TableColumn<Customer, PaymentStatus> colStatus;
    @FXML private TableColumn<Customer, String> colSurname;
    @FXML private TableColumn<Customer, Long> colId;
    @FXML private TableColumn<Customer, Void> colAction;
    @FXML private TableView<Customer> customerList;
    @FXML private Label lblTotalDebt;

    @Autowired private ICustomerService customerService;
    @Autowired private ITicketHistoryService ticketHistoryService;
    @Autowired private IRecordService recordService;
    @Autowired private EmailService emailService;
    @Autowired private ButtonManager buttonManager;
    @Autowired private ShowingStageValidation stageValidation;
    @Autowired private Session session;
    @Setter private HomeController homeController;

    private Stage customerRegister;
    private ObservableList<Customer> list;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        btnAddCustomer.setDisable(currentUser.getRole().equals(Rol.GUEST));
        setupSearchFilter();
        loadDataCustomerList();
        updateTotalDebtLabel();
    }

    @FXML
    private void handleButtonCustomer(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnAddCustomer)){
            if (stageValidation.validateStage("Crear Cliente")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/CustomerView/customerRegister.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();
            AddEditCustomerController addEditCustomerController = fxmlLoader.getController();
            addEditCustomerController.setPrincipal(btnAddCustomer);

            Scene scene = new Scene(root);
            customerRegister = new Stage();
            customerRegister.setScene(scene);
            customerRegister.setTitle("Crear Cliente");
            customerRegister.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/principalLogo.png"))));
            customerRegister.setResizable(false);
            customerRegister.initModality(Modality.APPLICATION_MODAL);
            buttonManager.selectedButtonStage(btnAddCustomer, scene, customerRegister);
            customerRegister.show();
        }
        homeController.handleButton(event);
    }

    private void editViewCustomer(Customer customer) throws IOException {
        if (stageValidation.validateStage("Actualización de Cliente")) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/CustomerView/customerRegister.fxml"));
        fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
        Parent root = fxmlLoader.load();
        AddEditCustomerController controller = fxmlLoader.getController();
        controller.setCurrentCustomer(customer);
        Scene scene = new Scene(root);
        customerRegister = new Stage();
        customerRegister.setScene(scene);
        customerRegister.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/principalLogo.png"))));
        customerRegister.setTitle("Actualización de Cliente");
        customerRegister.setResizable(false);
        customerRegister.initModality(Modality.APPLICATION_MODAL);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                customerRegister.close();
                controller.setCurrentCustomer(null);
            }
        });
        customerRegister.setOnCloseRequest(event -> {
            customerRegister.close();
            controller.setCurrentCustomer(null);
        });
        customerRegister.show();
    }

    private void paymentCustomerView(Customer customer) throws IOException {
        if (stageValidation.validateStage("Pago de Deudas")) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/CustomerView/payment.fxml"));
        fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
        Parent root = fxmlLoader.load();

        PaymentController controller = fxmlLoader.getController();
        controller.setCurrentCustomer(customer);

        Scene scene = new Scene(root);
        customerRegister = new Stage();
        customerRegister.setScene(scene);
        customerRegister.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/principalLogo.png"))));
        customerRegister.setTitle("Pago de Deudas");
        customerRegister.setResizable(false);
        customerRegister.initStyle(StageStyle.DECORATED);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                customerRegister.close();
            }
        });
        customerRegister.show();
    }

    public void loadDataCustomerList(){
        list = FXCollections.observableArrayList(customerService.getAll());
        customerList.setItems(list);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAccount.setCellValueFactory(new PropertyValueFactory<>("account"));
        colDebt.setCellValueFactory(new PropertyValueFactory<>("debt"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        for (Customer customer : list){
            ticketHistoryService.valideTicketByCustomer(customer);
            customerService.statusAssign(customer);
        }
        configurationTableView();
    }

    private void configurationTableView() {
        Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> cellFactory = new Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>>() {

            @Override
            public TableCell<Customer, Void> call(final TableColumn<Customer, Void> param) {
                return new TableCell<Customer, Void>() {
                    private final Button btnPay = buttonManager.createButton("btnPayIcon.png","green", 30, 30);
                    private final Button btnEdit = buttonManager.createButton("btnEditIcon.png","yellow", 30, 30);
                    private final Button btnEliminate = buttonManager.createButton("btnDeleteIcon.png", "red", 30, 30);
                    {
                        btnEliminate.setOnAction((event) -> {
                            Customer customer = getTableView().getItems().get(getIndex());
                            customerService.delete(customer);
                            loadDataCustomerList();
                            customerList.refresh();
                            recordService.registerAction(currentUser,"DELETE", "Elimino Cliente " + customer.getName() + " " + customer.getSurname());
                        });
                        btnEdit.setOnAction((event) -> {
                            Customer customer = getTableView().getItems().get(getIndex());
                            try {
                                editViewCustomer(customer);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            loadDataCustomerList();
                        });
                        btnPay.setOnAction((event) -> {
                            Customer customer = getTableView().getItems().get(getIndex());
                            try {
                                paymentCustomerView(customer);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            loadDataCustomerList();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Customer customer = getTableView().getItems().get(getIndex());
                            if (customer.getDebt() > 0 || !customer.isAccount() || currentUser.getRole().equals(Rol.GUEST)){
                                btnEliminate.setVisible(false);
                            }
                            btnEliminate.setDisable(currentUser.getRole().equals(Rol.SELLER));
                            btnEdit.setVisible(!currentUser.getRole().equals(Rol.GUEST));
                            btnPay.setVisible(customer.getDebt() > 0);

                            HBox buttons = new HBox(btnPay, btnEdit, btnEliminate);
                            buttons.setAlignment(Pos.CENTER);
                            buttons.setSpacing(4);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);

        colAccount.setCellFactory(column -> new TableCell<Customer,Boolean>(){
            @Override
            protected void updateItem(Boolean item,boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null){
                    setText("");
                }else {
                    setText(item ? "Activo" : "Inactivo");
                    autosize();
                }
            }
        });
        colDebt.setCellFactory(column -> new TableCell<Customer, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));
                    setText(currencyFormat.format(item));
                    autosize();
                    if (item > 0){
                        setTextFill(Color.RED);
                    }else{
                        setTextFill(Color.TRANSPARENT);
                    }
                }
            }
        });
        colId.setCellFactory(column -> new TableCell<Customer,Long>(){
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    autosize();
                }
            }
        });
        colEmail.setCellFactory(column -> new TableCell<Customer,String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setText(null);
                } else {
                    if (item.contains("x@x.xxx")) {
                        setText("Sin registro");

                    }else {
                        setText(item);
                    }
                    autosize();
                }
            }
        });
        colStatus.setCellFactory(column -> new TableCell<Customer,PaymentStatus>(){
            @Override
            protected void updateItem(PaymentStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty||item == PaymentStatus.NULO) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    if (item == PaymentStatus.PAGADA) {
                        setStyle("-fx-background-color: green;");
                    } else if (item == PaymentStatus.PENDIENTE) {
                        setStyle("-fx-background-color: red;");
                    } else if (item == PaymentStatus.PARCIAL) {
                        setStyle("-fx-background-color: yellow;");
                    } else if (item == PaymentStatus.VENCIDA) {
                        setStyle("-fx-background-color: #1a1aff;");
                    }
                    autosize();
                }
            }
        });
    }

    @FXML
    private void setupSearchFilter() {
        txtFindCustomer.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                customerList.setItems(list);
                customerList.refresh();
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                ObservableList<Customer> filteredList = FXCollections.observableArrayList();
                for (Customer customer : list) {
                    String customerStatus = customer.isAccount() ? "activo" : "inactivo";
                    if (customer.getName().toLowerCase().contains(lowerCaseFilter) ||
                            customer.getSurname().toLowerCase().contains(lowerCaseFilter) ||
                            customerStatus.equals(lowerCaseFilter)) {
                        filteredList.add(customer);
                    }
                }
                customerList.setItems(filteredList);
            }
        });
    }

    public void updateTotalDebtLabel() {
        int totalDebts = customerList.getItems().stream()
                .mapToInt(Customer::getDebt)
                .sum();
        lblTotalDebt.setText(String.format("$%,d", totalDebts));
    }
}