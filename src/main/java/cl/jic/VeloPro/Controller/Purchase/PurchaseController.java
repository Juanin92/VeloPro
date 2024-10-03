package cl.jic.VeloPro.Controller.Purchase;

import cl.jic.VeloPro.Controller.Product.IProductList;
import cl.jic.VeloPro.Model.DTO.DetailPurchaseDTO;
import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Entity.Product.SubcategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.UnitProduct;
import cl.jic.VeloPro.Model.Entity.Purchase.Purchase;
import cl.jic.VeloPro.Model.Entity.Purchase.PurchaseDetail;
import cl.jic.VeloPro.Model.Entity.Purchase.Supplier;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.MovementsType;
import cl.jic.VeloPro.Model.Enum.StatusProduct;
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
import javafx.application.Platform;
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
public class PurchaseController implements Initializable {

    @FXML private Button btnClean, btnSearchProduct, btnAddProduct, btnAddCategories;
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
    @FXML private CustomTextField txtDocument, txtIva, txtTotal, txtSearchFastProduct;
    @FXML private TableView<Product> listSearchProduct;
    @FXML private TableColumn<Product, String> colBrandPurchase;
    @FXML private TableColumn<Product, CategoryProduct> colCategory;
    @FXML private TableColumn<Product, String> colDescriptionPurchase;
    @FXML private TableColumn<Product, Long> colIdProductPurchase;
    @FXML private TableColumn<Product, StatusProduct> colStatus;
    @FXML private TableColumn<Product, SubcategoryProduct> colSubcategory;
    @FXML private TableColumn<Product, UnitProduct> colUnit;

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

    private ObservableList<Product> productsList;
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
        if (url.toString().contains("listProduct.fxml")){
            Platform.runLater(this::loadDataProductSearchList);
            setupSearchFilterProduct();
        }
    }

    @FXML
    private void handleButton(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnSearchProduct)){
            if (stageValidation.validateStage("Agregar Productos")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/listProduct.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Agregar Productos");
            stage.initStyle(StageStyle.UNDECORATED);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            });
            stage.show();
        }  else if (event.getSource().equals(btnClean)){
            cleanField();
            cleanListDto();
        } else if (event.getSource().equals(btnAddCategories)) {
            if (stageValidation.validateStage("Crear Categorías")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/categories.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Crear Categorías");
            stage.initStyle(StageStyle.UNDECORATED);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            });
            stage.show();
        } else if (event.getSource().equals(btnAddProduct)) {
            if (stageValidation.validateStage("Agregar Productos")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/products.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Agregar Productos");
            stage.initStyle(StageStyle.UNDECORATED);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            });
            stage.show();
        }
    }

    @FXML
    private void addPurchase(){
        try{
            if (!dtoList.isEmpty()){
                if (validateStockAndPrice()){
                    int total = Integer.parseInt(txtTotal.getText());
                    if (totalPricePurchaseList == total){
                        Purchase purchase = new Purchase();
                        LocalDate selectedDate = dtDate.getValue();
                        purchase.setDate(selectedDate);
                        purchase.setSupplier(cbSupplier.getValue());
                        RadioButton selectedRadioButton = (RadioButton) radioGroup.getSelectedToggle();
                        if (selectedRadioButton != null) {
                            purchase.setDocumentType(selectedRadioButton.getText());
                        }
                        purchase.setDocument(txtDocument.getText());
                        purchase.setIva((int) taxValue);
                        purchase.setPurchaseTotal(Integer.parseInt(txtTotal.getText()));
                        purchaseService.save(purchase);

                        for (DetailPurchaseDTO dto : dtoList) {
                            PurchaseDetail purchaseDetail = new PurchaseDetail();
                            purchaseDetail.setPrice(dto.getPrice());
                            purchaseDetail.setQuantity(dto.getQuantity());
                            purchaseDetail.setTax(dto.getTax());
                            purchaseDetail.setTotal(dto.getTotal());
                            purchaseDetail.setProduct(productService.getProductById(dto.getIdProduct()));
                            purchaseDetail.setPurchase(purchase);
                            purchaseDetailService.save(purchaseDetail);

                            Product product = productService.getProductById(dto.getIdProduct());
                            if (product != null) {
                                product.setBuyPrice(dto.getPrice());
                                product.setStock(product.getStock() + dto.getQuantity());
                                productService.update(product);
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
        Optional<DetailPurchaseDTO> optionalDto = dtoList.stream()
                .filter(dto -> Objects.equals(dto.getIdProduct(), idProduct))
                .findFirst();

        optionalDto.ifPresent(dto -> {
            dtoList.remove(dto);
            quantityProductPurchaseList -= 1;
            lblProductQuantity.setText(String.valueOf(quantityProductPurchaseList));
            updateTotalPurchase();
        });
    }

    public void loadDataDetailPurchaseList(ObservableList<DetailPurchaseDTO> dto){
        ObservableList<DetailPurchaseDTO> detailPurchaseList = FXCollections.observableArrayList(dto);
        purchaseProductTable.setItems(detailPurchaseList);

        colId.setCellValueFactory(new PropertyValueFactory<>("idProduct"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTax.setCellValueFactory(new PropertyValueFactory<>("tax"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        configurationTableView();
    }

    private void createDto(Product product){
        if (product != null){
            DetailPurchaseDTO dto = new DetailPurchaseDTO();
            dto.setIdProduct(product.getId());
            dto.setBrand(product.getBrand().getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(0);
            dto.setTax(0);
            dto.setTotal(0);
            dto.setQuantity(0);
            boolean exists = dtoList.stream().anyMatch(existingDto -> existingDto.getIdProduct().equals(dto.getIdProduct()));
            if (!exists) {
                dtoList.add(dto);
                purchaseProductTable.setItems(dtoList);
                loadDataDetailPurchaseList(dtoList);
                lblProductQuantity.setText(String.valueOf(quantityProductPurchaseList += 1));
            }else {
                notificationManager.informationNotification("Ya ha seleccionado este producto.", "", Pos.CENTER);
            }
        }
    }

    public void loadDataProductSearchList(){
        productsList = FXCollections.observableArrayList(productService.getAll());
        listSearchProduct.setItems(productsList);
        listSearchProduct.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        listSearchProduct.getSelectionModel().selectedItemProperty().addListener((observableValue, product, newValue) -> {
            createDto(newValue);
        });

        colIdProductPurchase.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBrandPurchase.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSubcategory.setCellValueFactory(new PropertyValueFactory<>("subcategoryProduct"));
        colDescriptionPurchase.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusProduct"));

        configurationTableView();
    }

    private void configurationTableView() {
        colStatus.setCellFactory(column -> new TableCell<Product, StatusProduct>(){
            @Override
            protected void updateItem(StatusProduct item,boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null){
                    setText("");
                }else {
                    if (item.equals(StatusProduct.DISPONIBLE)){
                        setStyle("-fx-background-color: #1fff4a;");
                    }else if (item.equals(StatusProduct.NODISPONIBLE)){
                        setStyle("-fx-background-color: #1fb4ff;");
                    }else {
                        setStyle("-fx-background-color: #ff1f1f;");
                    }
                    autosize();
                }
            }
        });
        colTotal.setCellFactory(column -> new TableCell<DetailPurchaseDTO, Integer>() {
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
        colTax.setCellFactory(column -> new TableCell<DetailPurchaseDTO, Integer>() {
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
            return new TableCell<DetailPurchaseDTO, Integer>() {
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
                                commitEdit(Integer.parseInt(textField.getText()));
                                purchaseProductTable.refresh();
                            }
                        });
                        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                            if (!newVal) {
                                commitEdit(Integer.parseInt(textField.getText()));
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
                    totalPricePurchaseList -= oldValue;
                    if(newValue > 0){
                        dtoList = getTableView().getItems();
                        dtoList.get(index).setPrice(newValue);
                        int tax = (int) (newValue * IVA);
                        dtoList.get(index).setTax(tax);
                        int quantity = dtoList.get(index).getQuantity();
                        int newTotal = (newValue + tax) * quantity;
                        dtoList.get(index).setTotal(newTotal);
                    }else{
                        graphicsValidator.settingAndValidationTextField(textField,true, "");
                        cancelEdit();
                    }
                    setText(currencyFormat.format(newValue));
                    setGraphic(null);
                    getTableView().refresh();
                    updateTotalPurchase();
                }

                private String getString() {
                    return getItem() == null ? "" : currencyFormat.format(getItem());
                }
            };
        });

        colQuantity.setCellFactory(column -> {
            CustomTextField textField = new CustomTextField();
            return new TableCell<DetailPurchaseDTO, Integer>() {
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
                                commitEdit(Integer.parseInt(textField.getText()));
                            }
                        });
                        textField.textProperty().addListener((obs, oldValue, newValue) -> {
                            if (!newValue.equals(getItem().toString())) {
                                commitEdit(Integer.parseInt(textField.getText()));
                            }
                        });
                        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                            if (!newVal) {
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
                            graphicsValidator.settingAndValidationTextField(textField,true, "");
                            cancelEdit();
                        }
                        setText(String.valueOf(newValue));
                        setGraphic(null);
                        updateTotalPurchase();
                    }catch (NumberFormatException ex){
                        graphicsValidator.settingAndValidationTextField(textField, true, "");
                        cancelEdit();
                    }
                }
            };
        });

        Callback<TableColumn<DetailPurchaseDTO, Void>, TableCell<DetailPurchaseDTO, Void>> cellFactory = new Callback<TableColumn<DetailPurchaseDTO, Void>, TableCell<DetailPurchaseDTO, Void>>() {

            @Override
            public TableCell<DetailPurchaseDTO, Void> call(final TableColumn<DetailPurchaseDTO, Void> param) {
                return new TableCell<DetailPurchaseDTO, Void>() {
                    private final Button btnDelete = buttonManager.createButton("btnDeleteIcon.png", "red", 10, 10);
                    {
                        btnDelete.setOnAction((event) -> {
                            DetailPurchaseDTO dto = getTableView().getItems().get(getIndex());
                            deleteProductFromDetailPurchase(dto.getIdProduct());
                            loadDataDetailPurchaseList(dtoList);
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

    @FXML
    private void setupSearchFilterProduct() {
        txtSearchFastProduct .textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                listSearchProduct.setItems(productsList);
                listSearchProduct.refresh();
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                ObservableList<Product> filteredList = FXCollections.observableArrayList();
                for (Product product : productsList) {
                    String brandName = product.getBrand().getName().toLowerCase();
                    if (product.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                            brandName.contains(lowerCaseFilter)) {
                        filteredList.add(product);
                    }
                }
                listSearchProduct.setItems(filteredList);
            }
        });
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

        List<Supplier> suppliers= supplierService.getAll();
        cbSupplier.getItems().setAll(suppliers);

        lblProductQuantity.setText(String.valueOf(quantityProductPurchaseList = 0));
        lblTotalPurchase.setText(String.valueOf(totalPricePurchaseList = 0));

        Long numberPurchase = purchaseService.totalPurchase();
        lblNumberPurchase.setText("N° " + numberPurchase);
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
        totalPricePurchaseList = dtoList.stream()
                .mapToInt(DetailPurchaseDTO::getTotal)
                .sum();
        lblTotalPurchase.setText(currencyFormat.format(totalPricePurchaseList));
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