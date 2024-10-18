package cl.jic.VeloPro.Controller.Sale;

import cl.jic.VeloPro.Controller.HomeController;
import cl.jic.VeloPro.Controller.Product.ProductListController;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.MovementsType;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Service.Report.Interfaces.IKardexService;
import cl.jic.VeloPro.Service.Sale.Interfaces.IPaymentService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.*;
import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.Product.*;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Model.Enum.PaymentMethod;
import cl.jic.VeloPro.Service.Costumer.Interface.ICostumerService;
import cl.jic.VeloPro.Service.Costumer.Interface.ITicketHistoryService;
import cl.jic.VeloPro.Service.Product.Interface.IProductService;
import cl.jic.VeloPro.Service.Sale.Interfaces.ISaleDetailService;
import cl.jic.VeloPro.Service.Sale.Interfaces.ISaleService;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import cl.jic.VeloPro.Validation.ShowingStageValidation;
import cl.jic.VeloPro.VeloProApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import lombok.Setter;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

@Component
public class SaleController implements Initializable {

    @FXML private Button btnCancelPay, btnCash, btnCredit, btnDebit, btnLoan, btnTransfer;
    @FXML private Button btnMixed, btnPay, btnProduct;
    @FXML private TableView<DetailSaleDTO> saleTable;
    @FXML private TableColumn<DetailSaleDTO, String> colCategory;
    @FXML private TableColumn<DetailSaleDTO, Integer> colPrice;
    @FXML private TableColumn<DetailSaleDTO, String> colProduct;
    @FXML private TableColumn<DetailSaleDTO, Integer> colQuantity;
    @FXML private TableColumn<DetailSaleDTO, Integer> colTotal;
    @FXML private TableColumn<DetailSaleDTO, String> colUnit;
    @FXML private TableColumn<DetailSaleDTO, Void> colAction;
    @FXML private Label lblCash, lblChange, lblChangeFixed, lblCostumer, lblDate, lblNumberSale;
    @FXML private Label lblTotal, lblTypePay, lblUser, lblDiscount, lblDiscountFixed;
    @FXML private Label lblRemainFixed, lblLoanFixed;
    @FXML private CustomTextField txtAmountCash, txtDiscount;
    @FXML private CheckBox cbDiscount;
    @FXML private AnchorPane saleTypePane, paneDiscount, paneAmount, paneLabels;
    @FXML private ComboBox<Costumer> cbCostumer;

    @Autowired private IProductService productService;
    @Autowired private ISaleService saleService;
    @Autowired private ISaleDetailService saleDetailService;
    @Autowired private IPaymentService paymentService;
    @Autowired private ICostumerService costumerService;
    @Autowired private ITicketHistoryService ticketHistoryService;
    @Autowired private IKardexService kardexService;
    @Autowired private IRecordService recordService;
    @Autowired private EmailService emailService;
    @Autowired private PDFGenerator pdfGenerator;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private ButtonManager buttonManager;
    @Autowired private NotificationManager notificationManager;
    @Autowired private ShowingStageValidation stageValidation;
    @Autowired private Session session;

    @Setter private HomeController homeController;
    @Setter private boolean isViewSelected = true;
    private Costumer selectedCostumer;
    private User currentUser;
    private final ObservableList<DetailSaleDTO> dtoList = FXCollections.observableArrayList();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));
    private int total;
    private int discount;
    private PaymentMethod active;
    private Long numberSale;
    private String comment = "";
    private Button selectedButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        btnLoan.setVisible(!currentUser.getRole().equals(Rol.GUEST));
        btnMixed.setVisible(!currentUser.getRole().equals(Rol.GUEST));
        graphicsValidator.handleTextfieldChange(txtAmountCash);
        loadData();
        loadCostumer();
    }

    @FXML
    private void handleButton(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnProduct)){
            if (stageValidation.validateStage("Agregar Productos")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/listProduct.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();
            ProductListController controller = fxmlLoader.getController();
            controller.setView("sale");
            controller.loadData();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Agregar Productos");
            stage.initStyle(StageStyle.UNDECORATED);
            buttonManager.selectedButtonStage(btnProduct, scene, stage);
            stage.show();
            isViewSelected = false;
        } else if (event.getSource().equals(btnCancelPay)) {
            handleButtonDisable(btnCancelPay);
        } else {
            Button clickedButton = (Button) event.getSource();
            if (selectedButton != null) {
                selectedButton.getStyleClass().remove("custom-button-selected");
            }
            clickedButton.getStyleClass().add("custom-button-selected");
            selectedButton = clickedButton;
            handleButtonDisable(clickedButton);
            handleElementsShow(clickedButton);

            if (clickedButton.equals(btnLoan)) {
                active = PaymentMethod.PRESTAMO;
            }
            if (clickedButton.equals(btnMixed)){
                active = PaymentMethod.MIXTO;
                mixedPayments();
            }
            if (clickedButton.equals(btnCash)) {
                active = PaymentMethod.EFECTIVO;
                cashPayments();
            } else if (clickedButton.equals(btnCredit)) {
                active = PaymentMethod.CREDITO;
            } else if (clickedButton.equals(btnDebit)) {
                active = PaymentMethod.DEBITO;
            } else if (clickedButton.equals(btnTransfer)) {
                active = PaymentMethod.TRANSFERENCIA;
            }
        }
        homeController.handleButton(event);
    }

    @FXML
    private void addSale(){
        try{
            int discountAmount = txtDiscount.getText().isEmpty() ? 0 : Integer.parseInt(txtDiscount.getText());
            int cashAmount = txtAmountCash.getText().isEmpty() ? 0 : Integer.parseInt(txtAmountCash.getText());
            Sale sale = saleService.addSale(discount, total, dtoList, discountAmount,
                    cbDiscount.isSelected(), selectedCostumer, numberSale, cashAmount, active);

            if (active == PaymentMethod.DEBITO || active == PaymentMethod.CREDITO) {
                handleDialogs( sale,"Comprobante", "Transacción");
            } else if (active == PaymentMethod.TRANSFERENCIA) {
                handleDialogs( sale,"Transferencia", "Transferencia");
            }
            saleService.addSale(sale);

            pdfGenerator.generateSaleReceiptPDF(sale, dtoList);
            String userHome = System.getProperty("user.home");
            String filePath = userHome + File.separator + "Documents" + File.separator + "Boletas" + File.separator + sale.getDocument() + ".pdf";

            if (active.equals(PaymentMethod.PRESTAMO) || active.equals(PaymentMethod.MIXTO)){
                costumerService.addSaleToCostumer(selectedCostumer);
                try{
                    emailService.sendEmailWithReceipt(selectedCostumer, sale, filePath);
                }catch (Exception e){
                    notificationManager.warningNotification("Error en el envío del correo", e.getMessage() + " " + selectedCostumer.getName(), Pos.CENTER);
                }
            }
            for (DetailSaleDTO dto : dtoList) {
                Product product = productService.getProductById(dto.getId());
                saleDetailService.createSaleDetails(dto, sale, product);
                if (product != null) {
                    productService.updateStockSale(product, dto.getQuantity());
                    kardexService.addKardex(product, dto.getQuantity(), "Venta", MovementsType.SALIDA, currentUser);
                }
            }
            notificationManager.successNotification("Venta Exitosa!", "La venta " + lblNumberSale.getText() + " se ha efectuado correctamente.", Pos.TOP_CENTER);
            recordService.registerAction(currentUser, "CREATE", "Crea Venta " + sale.getDocument());
            pdfGenerator.openPDF(sale,dtoList);
            resetAfterSale();
        }catch (Exception e){
            notificationManager.errorNotification("Error!", "Se ha producido un error en la venta " + lblNumberSale.getText() + "\n" + e.getMessage(), Pos.CENTER);
        }
    }

    public void loadDataDetailSaleList(ObservableList<DetailSaleDTO> dto){
        if (!dto.isEmpty()){
            saleTypePane.setDisable(false);
            ObservableList<DetailSaleDTO> detailSaleList = FXCollections.observableArrayList(dto);
            saleTable.setItems(detailSaleList);

            colProduct.setCellValueFactory(new PropertyValueFactory<>("description"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
            colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
            colPrice.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
            colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

            configurationTableView();
        }else {
            saleTypePane.setDisable(true);
        }
    }

    public void createDto(DetailSaleDTO dto){
        boolean exists = dtoList.stream().anyMatch(existingDto -> existingDto.getId().equals(dto.getId()));
        if (!exists) {
            dtoList.add(dto);
            saleTable.setItems(dtoList);
            loadDataDetailSaleList(dtoList);
            calculateTotal();
        }else {
            notificationManager.informationNotification("Ya ha seleccionado este producto.", "", Pos.CENTER);
        }
    }

    private void cashPayments(){
        paneAmount.setVisible(true);
        paneDiscount.setVisible(true);
        txtAmountCash.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try{
                    int cash = Integer.parseInt(txtAmountCash.getText());
                    int effectiveTotal = cbDiscount.isSelected() ? total - discount : total;
                    int change = paymentService.cashPayment(cash, effectiveTotal);
                    if (change >= 0) {
                        lblChange.setText(currencyFormat.format(change));
                        lblCash.setText(currencyFormat.format(cash));
                        btnPay.setDisable(false);
                    } else {
                        graphicsValidator.settingAndValidationFieldSale(txtAmountCash, true, "El monto ingresado no es correcto");
                        btnPay.setDisable(true);
                    }
                }catch (NumberFormatException e) {
                    graphicsValidator.settingAndValidationFieldSale(txtAmountCash, true, "Por favor ingrese un número válido.");
                    btnPay.setDisable(true);
                }
                txtAmountCash.clear();
            }
        });
        lblCash.setVisible(true);
        lblTypePay.setVisible(true);
        lblChange.setVisible(true);
        lblChangeFixed.setVisible(true);
    }

    private void mixedPayments(){
        cbCostumer.setVisible(true);
        txtAmountCash.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try{
                    int cash = Integer.parseInt(txtAmountCash.getText());
                    int effectiveTotal = cbDiscount.isSelected() ? total - discount : total;
                    int change = paymentService.mixedPayment(cash, effectiveTotal);
                    if (change > 0){
                        comment = String.valueOf(cash);
                        lblDiscount.setText(currencyFormat.format(cash));
                        total = change;
                        lblCash.setText(currencyFormat.format(change));
                        btnPay.setDisable(false);
                    }else {
                        graphicsValidator.settingAndValidationFieldSale(txtAmountCash, true, "El monto ingresado no es correcto");
                        btnPay.setDisable(true);
                    }
                }catch (NumberFormatException e){
                    graphicsValidator.settingAndValidationFieldSale(txtAmountCash, true, "Por favor ingrese un número válido.");
                    btnPay.setDisable(true);
                }
                txtAmountCash.clear();
            }
        });
    }

    private void calculateTotal() {
        total = saleService.calculateTotalSale(dtoList);
        lblTotal.setText(currencyFormat.format(total));
    }

    private void calculateDiscount(){
        int discountValue = 0;
        if (cbDiscount.isSelected()) {
            try{
                discountValue = saleService.calculateDiscountSale(total, Integer.parseInt(txtDiscount.getText()));
            }catch (Exception e){
                graphicsValidator.settingAndValidationTextField(txtDiscount, true, "El valor no válido");
            }
        }
        discount = discountValue;
        lblDiscount.setText(currencyFormat.format(discount));
        lblTotal.setText(currencyFormat.format(total - discount));
    }

    private void loadCostumer(){
        List<Costumer> costumerList = costumerService.getAll();
        ObservableList<Costumer> costumers = FXCollections.observableArrayList(costumerList);
        FilteredList<Costumer> filterCostumer = new FilteredList<>(costumers, Costumer::isAccount);
        cbCostumer.setItems(filterCostumer);
        cbCostumer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                lblCostumer.setText(newValue.toString());
                lblCostumer.setVisible(true);
                cbCostumer.setVisible(false);
                selectedCostumer = newValue;
                btnPay.setDisable(false);
            }
        });
    }

    private void deleteProductFromDetailSale(Long idProduct) {
        total = saleDetailService.deleteProduct(dtoList, idProduct, total);
        lblTotal.setText(currencyFormat.format(total));
        if (dtoList.isEmpty()) {
            saleTable.getItems().clear();
            saleTable.refresh();
            resetAfterSale();
            saleTypePane.setDisable(true);
        }
        calculateTotal();
    }

    private void configurationTableView() {
        colPrice.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(item));
                    autosize();
                }
            }
        });
        colTotal.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(item));
                    autosize();
                }
            }
        });
        colQuantity.setCellFactory(column -> {
            CustomTextField textField = new CustomTextField();
            return new TableCell<>() {
                @Override
                public void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(String.valueOf(item));
                        setGraphic(null);
                        autosize();
                        setOnMouseClicked(mouseEvent -> {
                            if (!isEmpty()) {
                                startEdit();
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
                            }
                        });
                        textField.textProperty().addListener((obs, oldValue, newValue) -> {
                            if (!newValue.equals(getItem().toString())) {
                                btnPay.setDisable(true);
                                commitEdit(Integer.parseInt(textField.getText()));
                            }
                        });
                        getGraphic().requestFocus();
                    }
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setText(String.valueOf(getItem()));
                    setGraphic(null);
                }

                @Override
                public void commitEdit(Integer newValue) {
                    super.commitEdit(newValue);
                    int index = getTableRow().getIndex();
                    DetailSaleDTO item = getTableView().getItems().get(index);
                    if(newValue >= 1){
                        if (item.getStock() > newValue){
                            item.setQuantity(newValue);
                            int newTotal = item.getQuantity() * item.getSalePrice();
                            item.setTotal(newTotal);
                            calculateTotal();
                            getTableView().refresh();
                            setText(String.valueOf(newValue));
                        }else{
                            item.setQuantity(item.getStock());
                            int maxTotal = item.getStock() * item.getSalePrice();
                            item.setTotal(maxTotal);
                            calculateTotal();
                            getTableView().refresh();
                            setText(String.valueOf(item.getStock()));
                            graphicsValidator.settingAndValidationTextField(textField,true, "");
                            cancelEdit();
                        }
                    }else{
                        if (newValue == 0){
                            setText("1");
                        }
                        setText(String.valueOf(item.getStock()));
                        graphicsValidator.settingAndValidationTextField(textField,true,"");
                        cancelEdit();
                    }
                    setGraphic(null);
                }
            };
        });

        Callback<TableColumn<DetailSaleDTO, Void>, TableCell<DetailSaleDTO, Void>> cellFactory = new Callback<TableColumn<DetailSaleDTO, Void>, TableCell<DetailSaleDTO, Void>>() {

            @Override
            public TableCell<DetailSaleDTO, Void> call(final TableColumn<DetailSaleDTO, Void> param) {
                return new TableCell<>() {
                    private final Button btnDelete = buttonManager.createButton("btnDeleteIcon.png", "red", 10, 10);
                    {
                        btnDelete.setOnAction((event) -> {
                            DetailSaleDTO dto = getTableView().getItems().get(getIndex());
                            deleteProductFromDetailSale(dto.getId());
                            loadDataDetailSaleList(dtoList);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(btnDelete);
                            buttons.setAlignment(Pos.CENTER);
                            buttons.setSpacing(5);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);
    }

    private void loadData() {
        try {
            lblUser.setText(String.valueOf(currentUser.getName()));
            LocalDate date = LocalDate.now();
            lblDate.setText(String.valueOf(date));
            numberSale = saleService.totalSales();
            lblNumberSale.setText("N° " + numberSale);
            lblTotal.setText(currencyFormat.format(0));
            paneLabels.setVisible(false);
            btnPay.setDisable(true);
            btnCancelPay.setDisable(true);
            if (isViewSelected){
                dtoList.clear();
            }

            cbDiscount.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    txtDiscount.setVisible(true);
                    lblDiscountFixed.setVisible(true);
                    lblDiscount.setVisible(true);
                    txtDiscount.setOnKeyPressed(keyEvent -> {
                        if (keyEvent.getCode() == KeyCode.ENTER) {
                            calculateDiscount();
                        }
                    });
                } else {
                    txtDiscount.setVisible(false);
                    lblDiscountFixed.setVisible(false);
                    lblDiscount.setVisible(false);
                    txtDiscount.setText("");
                    lblDiscount.setText("0");
                    lblTotal.setText(currencyFormat.format(total));
                }
                lblCash.setText("0");
                lblChange.setText("0");
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Se produjo un error al cargar la información");
        }
    }

    private void resetAfterSale() {
        dtoList.clear();
        saleTable.getItems().clear();
        saleTable.refresh();
        loadData();
        selectedButton = null;
        handleButtonDisable(btnPay);

        cbCostumer.getSelectionModel().clearSelection();
        paneDiscount.setVisible(false);
        lblTotal.setText(currencyFormat.format(0));
        discount = 0;
        paneLabels.setVisible(false);
        saleTypePane.setDisable(true);
    }

    private void handleButtonDisable(Button selectedButton) {
        saleTypePane.setDisable(false);
        btnPay.setDisable(false);
        btnCancelPay.setDisable(false);
        paneDiscount.setVisible(true);
        paneLabels.setVisible(true);

        if (selectedButton.equals(btnCancelPay) || selectedButton.equals(btnPay)){
            if (this.selectedButton != null) {
                this.selectedButton.getStyleClass().remove("custom-button-selected");
            }
            this.selectedButton = null;
            btnCancelPay.setDisable(true);
            btnPay.setDisable(true);
            saleTypePane.setDisable(true);
            lblTotal.setText(currencyFormat.format(0));
            lblCash.setText(currencyFormat.format(0));
            lblChange.setText(currencyFormat.format(0));
            lblDiscount.setText(currencyFormat.format(0));
            paneAmount.setVisible(false);
            paneDiscount.setVisible(false);
            selectedCostumer = null;
            lblCostumer.setVisible(false);
            cbCostumer.setVisible(false);
            paneLabels.setVisible(false);
            dtoList.clear();
            saleTable.getItems().clear();
            saleTable.refresh();
            cbDiscount.setSelected(false);
        }
        if (selectedButton.equals(btnMixed)){
            paneAmount.setVisible(true);
            lblRemainFixed.setVisible(true);
            lblLoanFixed.setVisible(true);
            lblChangeFixed.setVisible(false);
            lblTypePay.setVisible(false);
            lblDiscount.setVisible(true);
            lblCash.setVisible(true);
            paneDiscount.setVisible(false);
            btnPay.setDisable(true);
        }
        if (selectedButton.equals(btnCash)){
            btnPay.setDisable(true);
        }
    }

    private void handleElementsShow(Button btn){
        lblCash.setText(currencyFormat.format(0));
        lblChange.setText(currencyFormat.format(0));
        lblDiscount.setText(currencyFormat.format(0));
        if (btn.equals(btnCash)){
            cbDiscount.setVisible(true);
            cbCostumer.setVisible(false);
            lblCostumer.setVisible(false);
            lblTypePay.setVisible(true);
            lblChangeFixed.setVisible(true);
            lblChange.setVisible(true);
            lblDiscount.setVisible(false);
            lblRemainFixed.setVisible(false);
            lblLoanFixed.setVisible(false);
            cbDiscount.setSelected(false);
        } else if (btn.equals(btnLoan)) {
            cbDiscount.setVisible(true);
            cbCostumer.setVisible(true);
            lblTypePay.setVisible(false);
            lblLoanFixed.setVisible(true);
            lblChangeFixed.setVisible(false);
            lblChange.setVisible(false);
            lblDiscount.setVisible(false);
            lblCash.setVisible(true);
            lblCash.setText(lblTotal.getText());
            lblRemainFixed.setVisible(false);
            paneAmount.setVisible(false);
            cbDiscount.setSelected(false);
        } else if (btn.equals(btnMixed)) {
            cbCostumer.setVisible(true);
            paneDiscount.setVisible(false);
            lblRemainFixed.setVisible(true);
            lblTypePay.setVisible(false);
            lblChangeFixed.setVisible(false);
            lblChange.setVisible(false);
            lblLoanFixed.setVisible(true);
            cbDiscount.setSelected(false);
            lblDiscount.setVisible(true);
        } else {
            cbCostumer.setVisible(false);
            cbDiscount.setVisible(true);
            cbDiscount.setSelected(false);
            lblTypePay.setVisible(false);
            lblLoanFixed.setVisible(false);
            lblChangeFixed.setVisible(false);
            lblCash.setVisible(false);
            lblChange.setVisible(false);
            lblDiscount.setVisible(false);
            lblRemainFixed.setVisible(false);
            paneAmount.setVisible(false);
        }
    }

    private void handleDialogs(Sale sale, String message, String action) throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle("Confirmar " + action);
        dialog.setHeaderText("Ingrese el número de " + message + " para confirmar la venta");
        dialog.setContentText("Número de " + message + ": ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            sale.setComment(message + ": " + result.get().trim());
        } else {
            resetAfterSale();
            throw new Exception("Operación cancelada. No se ingresó un número de comprobante.");
        }
    }
}