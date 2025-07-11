package cl.jic.VeloPro.Controller.Purchase;

import cl.jic.VeloPro.Controller.Product.IProductList;
import cl.jic.VeloPro.Controller.Product.ProductListController;
import cl.jic.VeloPro.Model.DTO.DetailPurchaseDTO;
import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Entity.Purchase.Purchase;
import cl.jic.VeloPro.Model.Entity.Purchase.Supplier;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.MovementsType;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Service.Report.Interfaces.IKardexService;
import cl.jic.VeloPro.Service.Purchase.Interfaces.IPurchaseDetailService;
import cl.jic.VeloPro.Service.Purchase.Interfaces.IPurchaseService;
import cl.jic.VeloPro.Service.Purchase.Interfaces.ISupplierService;
import cl.jic.VeloPro.Service.Product.Interface.IProductService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.ButtonManager;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.Validation.GraphicsValidator;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

@Component
public class PurchaseController implements Initializable{

    @FXML private Button btnClean, btnSearchProduct;
    @FXML private ComboBox<Supplier> cbSupplier;
    @FXML private TableView<DetailPurchaseDTO> purchaseProductTable;
    @FXML private TableColumn<DetailPurchaseDTO, Void> colAction;
    @FXML private TableColumn<DetailPurchaseDTO, String> colBrand;
    @FXML private TableColumn<DetailPurchaseDTO, String> colDescription;
    @FXML private TableColumn<DetailPurchaseDTO, Long> colId;
    @FXML private TableColumn<DetailPurchaseDTO, Integer> colTax;
    @FXML private TableColumn<DetailPurchaseDTO, Integer> colQuantity;
    @FXML private TableColumn<DetailPurchaseDTO, Integer> colPrice;
    @FXML private TableColumn<DetailPurchaseDTO, Integer> colTotal;
    @FXML private DatePicker dtDate;
    @FXML private Label lblNumberPurchase, lblProductQuantity, lblTotalPurchase, lblTotalDoc, lblDocument;
    @FXML private RadioButton rbInvoice, rbReceipt;
    @FXML private ToggleGroup radioGroup;
    @FXML private CustomTextField txtDocument, txtIva, txtTotal;

    @Autowired private IProductService productService;
    @Autowired private IPurchaseService purchaseService;
    @Autowired private ISupplierService supplierService;
    @Autowired private IPurchaseDetailService purchaseDetailService;
    @Autowired private IProductList productList;
    @Autowired private IKardexService kardexService;
    @Autowired private IRecordService recordService;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private ButtonManager buttonManager;
    @Autowired private NotificationManager notificationManager;
    @Autowired private ShowingStageValidation stageValidation;
    @Autowired private Session session;

    private ObservableList<DetailPurchaseDTO> dtoList = FXCollections.observableArrayList();
    private int quantityProductPurchaseList = 0;
    private int totalPricePurchaseList = 0;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));
    private final double IVA = 0.19;
    private double taxValue;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        dtoList.clear();
        loadData();
    }

    @FXML
    private void handleButton(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnSearchProduct)){
            if (stageValidation.validateStage("Agregar Productos")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/listProduct.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();
            ProductListController controller = fxmlLoader.getController();
            controller.setView("purchase");
            controller.loadData();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/principalLogo.png"))));
            stage.setTitle("Agregar Productos");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            buttonManager.selectedButtonStage(btnSearchProduct, scene, stage);
            stage.show();
        }  else if (event.getSource().equals(btnClean)){
            cleanField();
            cleanListDto();
        }
    }

    @FXML
    private void addPurchase(){
        try{
            if (!dtoList.isEmpty()){
                if (validateStockAndPrice()){
                    int total = Integer.parseInt(txtTotal.getText());
                    totalPricePurchaseList = purchaseService.totalPricePurchase(dtoList);
                    if (totalPricePurchaseList == total){
                        LocalDate selectedDate = dtDate.getValue();
                        RadioButton selectedRadioButton = (RadioButton) radioGroup.getSelectedToggle();
                        Purchase purchase = purchaseService.save(selectedDate, cbSupplier.getValue(),
                                selectedRadioButton.getText(), txtDocument.getText(), (int) taxValue, Integer.parseInt(txtTotal.getText()));

                        for (DetailPurchaseDTO dto : dtoList) {
                            Product product = productService.getProductById(dto.getIdProduct());
                            purchaseDetailService.createDetailPurchase(dto, purchase, product);
                            if (product != null) {
                                productService.updateStockPurchase(product, dto.getPrice(), dto.getQuantity());
                                productList.loadDataStockList();
                                kardexService.addKardex(product, dto.getQuantity(), "Compra", MovementsType.ENTRADA, currentUser);
                            }
                        }
                        cleanListDto();
                        cleanField();

                        notificationManager.successNotification("Registro Exitoso!", "La compra ha sido agregada al sistema correctamente.", Pos.CENTER);
                        recordService.registerAction(currentUser, "CREATE", "Crear Compra" + purchase.getDocument());
                    }else {
                        notificationManager.errorNotification("Valores erróneo!","Verifique el total de la compra con el total de los producto. \nHay una diferencia de $" + (total - totalPricePurchaseList), Pos.CENTER);
                    }
                }
            }else {
                notificationManager.errorNotification("No existen datos!", "Debe agregar producto a la lista",Pos.CENTER);
            }
        }catch (Exception e){
           handleValidationException(e.getMessage());
        }
    }

    private void deleteProductFromDetailPurchase(Long idProduct){
        if(purchaseDetailService.deleteProduct(idProduct,dtoList)){
            quantityProductPurchaseList -= 1;
            lblProductQuantity.setText(String.valueOf(quantityProductPurchaseList));
            updateTotalPurchase();
        }
    }

    public void loadDataDetailPurchaseList(){
        colId.setCellValueFactory(new PropertyValueFactory<>("idProduct"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTax.setCellValueFactory(new PropertyValueFactory<>("tax"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        configurationTableView();
    }

    public void createDto(DetailPurchaseDTO dto){
        if (dto != null) {
            boolean exists = dtoList.stream().anyMatch(existingDto -> existingDto.getIdProduct().equals(dto.getIdProduct()));
            if (!exists) {
                dtoList.add(dto);
                purchaseProductTable.setItems(dtoList);
                loadDataDetailPurchaseList();
                lblProductQuantity.setText(String.valueOf(quantityProductPurchaseList += 1));
            } else {
                notificationManager.informationNotification("Ya ha seleccionado este producto.", "", Pos.CENTER);
            }
        }
    }

    private void configurationTableView() {
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
        colTax.setCellFactory(column -> new TableCell<>() {
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
        colPrice.setCellFactory(column -> {
            CustomTextField textField = new CustomTextField();
            return new TableCell<>() {
                @Override
                public void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(currencyFormat.format(item));
                        setGraphic(null);
                        autosize();
                        if (item == 0){
                            startEdit();
                        }
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
                                int newValue = textfieldValue(textField.getText());
                                commitEdit(newValue);
                                purchaseProductTable.refresh();
                            }
                        });
                        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                            if (!newVal) {
                                int newValue = textfieldValue(textField.getText());
                                commitEdit(newValue);
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
                    int oldValue = dtoList.get(index).getPrice();
                    if(newValue > 0){
                        dtoList = getTableView().getItems();
                        dtoList.get(index).setPrice(newValue);
                        int tax = (int) (newValue * IVA);
                        dtoList.get(index).setTax(tax);
                        int quantity = dtoList.get(index).getQuantity();
                        int newTotal = (newValue + tax) * quantity;
                        dtoList.get(index).setTotal(newTotal);
                    }else{
                        cancelEdit();
                    }
                    setText(currencyFormat.format(newValue));
                    setGraphic(null);
                    getTableView().refresh();
                    updateTotalPurchase();
                }

                private int textfieldValue(String text) {
                    if (text == null || text.isEmpty()) {
                        return 0;
                    }
                    try {
                        return Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                }

                private String getString() {
                    return getItem() == null ? "" : currencyFormat.format(getItem());
                }
            };
        });

        colQuantity.setCellFactory(column -> {
            CustomTextField textField = new CustomTextField();
            return new TableCell<>() {
                @Override
                public void updateItem(Integer item, boolean empty)     {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(String.valueOf(item));
                        setGraphic(null);
                        autosize();
                        if (item == 0){
                            startEdit();
                        }
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
                                int newValue = textfieldValue(textField.getText());
                                commitEdit(newValue);
                            }
                        });
                        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                            if (!newVal) {
                                int newValue = textfieldValue(textField.getText());
                                commitEdit(newValue);
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
                    try {
                        super.commitEdit(newValue);
                        if(newValue != null && newValue > 0){
                            int index = getTableRow().getIndex();
                            dtoList = getTableView().getItems();
                            dtoList.get(index).setQuantity(newValue);

                            int price = dtoList.get(index).getPrice();
                            int tax = dtoList.get(index).getTax();
                            int newTotal = (price + tax) * newValue;
                            dtoList.get(index).setTotal(newTotal);
                            getTableView().refresh();
                        }else{
                            cancelEdit();
                        }
                        setText(String.valueOf(newValue));
                        setGraphic(null);
                        updateTotalPurchase();
                    }catch (NumberFormatException ex){
                        cancelEdit();
                    }
                }

                private int textfieldValue(String text) {
                    if (text == null || text.isEmpty()) {
                        return 1;
                    }
                    try {
                        return Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        return 1;
                    }
                }
            };
        });

        Callback<TableColumn<DetailPurchaseDTO, Void>, TableCell<DetailPurchaseDTO, Void>> cellFactory = new Callback<TableColumn<DetailPurchaseDTO, Void>, TableCell<DetailPurchaseDTO, Void>>() {

            @Override
            public TableCell<DetailPurchaseDTO, Void> call(final TableColumn<DetailPurchaseDTO, Void> param) {
                return new TableCell<>() {
                    private final Button btnDelete = buttonManager.createButton("btnDeleteIcon.png", "red", 10, 10);
                    {
                        btnDelete.setOnAction((event) -> {
                            DetailPurchaseDTO dto = getTableView().getItems().get(getIndex());
                            deleteProductFromDetailPurchase(dto.getIdProduct());
                            loadDataDetailPurchaseList();
                            updateTotalPurchase();
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

    private void loadData(){
        graphicsValidator.handleTextfieldChangeWithLabel(txtDocument, lblDocument);
        graphicsValidator.handleTextfieldChangeWithLabel(txtTotal, lblTotalDoc);
        txtTotal.textProperty().addListener((observable, oldValue, newValue) -> {
            double total = Double.parseDouble(newValue);
            taxValue = (total / 1.19)* IVA;
            txtIva.setText(currencyFormat.format(taxValue));
        });
        cbSupplier.valueProperty().addListener((observable, oldValue, newValue) -> graphicsValidator.settingAndValidationComboBox(cbSupplier, false, ""));
        dtDate.valueProperty().addListener((observable, oldValue, newValue) -> graphicsValidator.settingAndValidationDatePurchase(dtDate, false, ""));

        radioGroup = new ToggleGroup();
        rbInvoice.setToggleGroup(radioGroup);
        rbReceipt.setToggleGroup(radioGroup);

        cbSupplier.getItems().setAll(supplierService.getAll());
        lblProductQuantity.setText(String.valueOf(quantityProductPurchaseList = 0));
        lblTotalPurchase.setText(String.valueOf(totalPricePurchaseList = 0));
        lblNumberPurchase.setText("N° " + purchaseService.totalPurchase());
    }

    private boolean validateStockAndPrice(){
        for (DetailPurchaseDTO dto : dtoList) {
            if (dto.getTotal() <= 0) {
                notificationManager.warningNotification("Valores inválidos!", "Verifique precio del producto: " + dto.getBrand(),Pos.CENTER);
                return false;
            } else if (dto.getQuantity() == 0) {
                notificationManager.warningNotification("Valores inválidos!", "Verifique la cantidad del producto: " + dto.getBrand(), Pos.CENTER);
                return false;
            }
        }
        return true;
    }

    private void updateTotalPurchase() {
        lblTotalPurchase.setText(currencyFormat.format(purchaseService.totalPricePurchase(dtoList)));
    }

    private void handleValidationException(String errorMessage){
        switch (errorMessage){
            case "Ingrese una fecha":
                graphicsValidator.settingAndValidationDatePurchase(dtDate, true, errorMessage);
                break;
            case "Seleccione un proveedor":
                graphicsValidator.settingAndValidationComboBox(cbSupplier, true, errorMessage);
                break;
            case "Ingrese Número de Documento.":
                graphicsValidator.settingAndValidationTextField(txtDocument, true, errorMessage);
                break;
            case "Ingrese sólo números":
                graphicsValidator.settingAndValidationTextField(txtTotal,true, errorMessage);
                break;
        }
    }

    private void cleanListDto() {
        dtoList.clear();
        totalPricePurchaseList = 0;
        quantityProductPurchaseList = 0;
        purchaseProductTable.getItems().clear();
        purchaseProductTable.refresh();
    }

    private void cleanField(){
        dtDate.setValue(null);
        cbSupplier.setValue(null);
        txtDocument.clear();
        txtTotal.clear();
        lblProductQuantity.setText("0");
        lblTotalPurchase.setText("0");
        purchaseProductTable.getItems().clear();
        purchaseProductTable.refresh();
    }
}