package cl.jic.VeloPro.Controller.Costumer;

import cl.jic.VeloPro.Controller.HomeController;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.ButtonManager;
import cl.jic.VeloPro.Utility.EmailService;
import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.Costumer.TicketHistory;
import cl.jic.VeloPro.Model.Enum.PaymentStatus;
import cl.jic.VeloPro.Service.Costumer.Interface.ICostumerService;
import cl.jic.VeloPro.Service.Costumer.Interface.ITicketHistoryService;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class CostumerController implements Initializable, ICostumerList {

    @FXML private Button btnAddCostumer;
    @FXML private TextField txtFindCostumer;
    @FXML private TableColumn<Costumer, Integer> colDebt;
    @FXML private TableColumn<Costumer, String> colEmail;
    @FXML private TableColumn<Costumer, String> colName;
    @FXML private TableColumn<Costumer, String> colPhone;
    @FXML private TableColumn<Costumer, Boolean> colAccount;
    @FXML private TableColumn<Costumer, PaymentStatus> colStatus;
    @FXML private TableColumn<Costumer, String> colSurname;
    @FXML private TableColumn<Costumer, Long> colId;
    @FXML private TableColumn<Costumer, Void> colAction;
    @FXML private TableView<Costumer> costumerList;
    @FXML private Label lblTotalDebt;

    @Autowired private ICostumerService costumerService;
    @Autowired private ITicketHistoryService ticketHistoryService;
    @Autowired private EmailService emailService;
    @Autowired private ButtonManager buttonManager;
    @Autowired private ShowingStageValidation stageValidation;
    @Autowired private Session session;
    @Setter private HomeController homeController;

    private Stage costumerRegister;
    private ObservableList<Costumer> list;
    private Costumer currentCostumer;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        btnAddCostumer.setDisable(currentUser.getRole().equals(Rol.GUEST));
        configurationTableView();
        setupSearchFilter();
        loadDataCostumerList();
        updateTotalDebt();
    }

    @FXML
    private void handleButtonCostumer(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnAddCostumer)){
            if (stageValidation.validateStage("Crear Cliente")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/CostumerView/costumerRegister.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();
            AddEditCostumerController addEditCostumerController = fxmlLoader.getController();
            addEditCostumerController.setPrincipal(btnAddCostumer);

            Scene scene = new Scene(root);
            costumerRegister = new Stage();
            costumerRegister.setScene(scene);
            costumerRegister.setTitle("Crear Cliente");
            costumerRegister.initStyle(StageStyle.UNDECORATED);
            buttonManager.selectedButtonStage(btnAddCostumer, scene, costumerRegister);
            costumerRegister.show();
        }
        homeController.handleButton(event);
    }

    private void editViewCostumer(Costumer costumer) throws IOException {
        if (stageValidation.validateStage("Actualización de Cliente")) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/CostumerView/costumerRegister.fxml"));
        fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
        Parent root = fxmlLoader.load();

        AddEditCostumerController controller = fxmlLoader.getController();
        controller.setCurrentCostumer(costumer);

        Scene scene = new Scene(root);
        costumerRegister = new Stage();
        costumerRegister.setScene(scene);
        costumerRegister.setTitle("Actualización de Cliente");
        costumerRegister.initStyle(StageStyle.UNDECORATED);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                costumerRegister.close();
            }
        });
        costumerRegister.show();
    }

    private void paymentCostumerView(Costumer costumer) throws IOException {
        if (stageValidation.validateStage("Pago de Deudas")) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/CostumerView/payment.fxml"));
        fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
        Parent root = fxmlLoader.load();

        PaymentController controller = fxmlLoader.getController();
        controller.setCurrentCostumer(costumer);

        Scene scene = new Scene(root);
        costumerRegister = new Stage();
        costumerRegister.setScene(scene);
        costumerRegister.setTitle("Pago de Deudas");
        costumerRegister.initStyle(StageStyle.UNDECORATED);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                costumerRegister.close();
            }
        });
        costumerRegister.show();
    }

    private void validateTicketCostumer(Costumer costumer){
        List<TicketHistory> tickets = ticketHistoryService.getByCostumerId(costumer.getId());
        for (TicketHistory ticket : tickets){
            if (ticketHistoryService.validateDate(ticket)){
                costumer.setStatus(PaymentStatus.VENCIDA);
                costumerService.updateTotalDebt(costumer);
                emailService.sendEmailDebtDelay(costumer,ticket);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // Ejecutar a las 00:00 todos los días
    public void validateAllTickets() {
        List<Costumer> customers = costumerService.getAll();
        for (Costumer customer : customers) {
            validateTicketCostumer(customer);
        }
    }

    public void loadDataCostumerList(){
        list = FXCollections.observableArrayList(costumerService.getAll());
        costumerList.setItems(list);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAccount.setCellValueFactory(new PropertyValueFactory<>("account"));
        colDebt.setCellValueFactory(new PropertyValueFactory<>("debt"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        for (Costumer costumer : list){
            validateTicketCostumer(costumer);
        }

        costumerList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentCostumer = newValue;
                validateTicketCostumer(currentCostumer);
                costumerService.statusAssign(currentCostumer);
            }
        });

        configurationTableView();
    }

    private void configurationTableView() {
        Callback<TableColumn<Costumer, Void>, TableCell<Costumer, Void>> cellFactory = new Callback<TableColumn<Costumer, Void>, TableCell<Costumer, Void>>() {

            @Override
            public TableCell<Costumer, Void> call(final TableColumn<Costumer, Void> param) {
                return new TableCell<Costumer, Void>() {
                    private final Button btnPay = buttonManager.createButton("btnPayIcon.png","green", 30, 30);
                    private final Button btnEdit = buttonManager.createButton("btnEditIcon.png","yellow", 30, 30);
                    private final Button btnEliminate = buttonManager.createButton("btnDeleteIcon.png", "red", 30, 30);
                    {
                        btnEliminate.setOnAction((event) -> {
                            Costumer costumer = getTableView().getItems().get(getIndex());
                            costumerService.delete(costumer);
                            loadDataCostumerList();
                            costumerList.refresh();
                        });
                        btnEdit.setOnAction((event) -> {
                            Costumer costumer = getTableView().getItems().get(getIndex());
                            try {
                                editViewCostumer(costumer);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            loadDataCostumerList();
                        });
                        btnPay.setOnAction((event) -> {
                            Costumer costumer = getTableView().getItems().get(getIndex());
                            try {
                                paymentCostumerView(costumer);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            loadDataCostumerList();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Costumer costumer = getTableView().getItems().get(getIndex());
                            if (costumer.getDebt() > 0){
                                btnEliminate.setDisable(true);
                                btnPay.setDisable(false);
                            } else {
                                btnEliminate.setDisable(false);
                                btnPay.setDisable(true);
                            }
                            btnEliminate.setVisible(costumer.isAccount());
                            btnEliminate.setDisable(currentUser.getRole().equals(Rol.SELLER));
                            btnEdit.setVisible(!currentUser.getRole().equals(Rol.GUEST));
                            btnEliminate.setVisible(!currentUser.getRole().equals(Rol.GUEST));
                            btnPay.setVisible(costumer.isAccount());

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

        colAccount.setCellFactory(column -> new TableCell<Costumer,Boolean>(){
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
        colDebt.setCellFactory(column -> new TableCell<Costumer, Integer>() {
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
        colId.setCellFactory(column -> new TableCell<Costumer,Long>(){
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
        colEmail.setCellFactory(column -> new TableCell<Costumer,String>(){
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
        colStatus.setCellFactory(column -> new TableCell<Costumer,PaymentStatus>(){
            @Override
            protected void updateItem(PaymentStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty||item == PaymentStatus.NULO) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    if (item == PaymentStatus.PAGADA) {
                        setTextFill(Color.BLACK);
                        setStyle("-fx-background-color: green;");
                    } else if (item == PaymentStatus.PENDIENTE) {
                        setTextFill(Color.BLACK);
                        setStyle("-fx-background-color: red;");
                    } else if (item == PaymentStatus.PARCIAL) {
                        setTextFill(Color.BLACK);
                        setStyle("-fx-background-color: yellow;");
                    } else if (item == PaymentStatus.VENCIDA) {
                        setTextFill(Color.BLACK);
                        setStyle("-fx-background-color: #1a1aff;");
                    }
                    setText(item.toString());
                    autosize();
                }
            }
        });
    }

    @FXML
    private void setupSearchFilter() {
        txtFindCostumer .textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                costumerList.setItems(list);
                costumerList.refresh();
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                ObservableList<Costumer> filteredList = FXCollections.observableArrayList();
                for (Costumer costumer : list) {
                    String costumerStatus = costumer.isAccount() ? "activo" : "inactivo";
                    if (costumer.getName().toLowerCase().contains(lowerCaseFilter) ||
                            costumer.getSurname().toLowerCase().contains(lowerCaseFilter) ||
                            costumerStatus.equals(lowerCaseFilter)) {
                        filteredList.add(costumer);
                    }
                }
                costumerList.setItems(filteredList);
            }
        });
    }

    public void updateTotalDebt() {
        int totalDebts = costumerList.getItems().stream()
                .mapToInt(Costumer::getDebt)
                .sum();
        lblTotalDebt.setText(String.format("$%,d", totalDebts));
        lblTotalDebt.setStyle("-fx-text-fill: red;");
    }
}