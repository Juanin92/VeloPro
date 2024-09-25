package cl.jic.VeloPro.Controller.Product;

import cl.jic.VeloPro.Controller.HomeController;
import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Entity.Product.SubcategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.UnitProduct;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Product.Interface.IProductService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.ButtonManager;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import lombok.Setter;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class StockController implements Initializable, IProductList{

    @FXML private Button btnAddCategories, btnAddProduct, btnSupplier, btnPurchase;
    @FXML private TableView<Product> listAllProduct;
    @FXML private TableColumn<Product, String> colBrand;
    @FXML private TableColumn<Product, Integer> colBuyPrice;
    @FXML private TableColumn<Product, CategoryProduct> colCategory;
    @FXML private TableColumn<Product, String> colDescription;
    @FXML private TableColumn<Product, Long> colId;
    @FXML private TableColumn<Product, Integer> colSalePrice;
    @FXML private TableColumn<Product, Boolean> colStatus;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, SubcategoryProduct> colSubcategory;
    @FXML private TableColumn<Product, UnitProduct> colUnit;
    @FXML private TableColumn<Product, Void> colAction;
    @FXML private CustomTextField txtSearchProduct;

    @Autowired private IProductService productService;
    @Autowired private ButtonManager buttonManager;
    @Autowired private ShowingStageValidation validateStage;
    @Autowired private Session session;

    @Setter private HomeController homeController;
    private ObservableList<Product> list;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        loadDataStockList();
        setupSearchFilter();
        managedUserView(currentUser);
    }

    @FXML
    private void handleButtonCostumer(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnAddProduct)){
            if (validateStage.validateStage("Agregar Productos")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/products.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();
            ProductController controller = fxmlLoader.getController();
            controller.setBtnAddProduct(btnAddProduct);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Agregar Productos");
            stage.initStyle(StageStyle.UNDECORATED);
            buttonManager.selectedButtonStage(btnAddProduct, scene, stage);
            stage.show();
        } else if (event.getSource().equals(btnAddCategories)) {
            if (validateStage.validateStage("Crear Categorías")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/categories.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Crear Categorías");
            stage.initStyle(StageStyle.UNDECORATED);
            buttonManager.selectedButtonStage(btnAddCategories, scene, stage);
            stage.show();
        } else if (event.getSource().equals(btnSupplier)) {
            if (validateStage.validateStage("Proveedores")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/supplier.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Proveedores");
            stage.initStyle(StageStyle.UNDECORATED);
            buttonManager.selectedButtonStage(btnSupplier, scene, stage);
            stage.show();
        } else if (event.getSource().equals(btnPurchase)) {
            if (validateStage.validateStage("Compras")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/purchase.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Compras");
            stage.initStyle(StageStyle.DECORATED);
            buttonManager.selectedButtonStage(btnPurchase, scene, stage);
            stage.show();
        }
        homeController.handleButton(event);
    }

    private void updateProductView(Product product) throws IOException {
        if (validateStage.validateStage("Actualización Productos")) return;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/products.fxml"));
        fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
        Parent root = fxmlLoader.load();

        ProductController controller = fxmlLoader.getController();
        controller.setSelectedProduct(product);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Actualización Productos");
        stage.initStyle(StageStyle.UNDECORATED);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                stage.close();
                controller.setSelectedProduct(null);
            }
        });
        stage.show();
    }

    public void loadDataStockList(){
        list = FXCollections.observableArrayList(productService.getAll());
        listAllProduct.setItems(list);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSubcategory.setCellValueFactory(new PropertyValueFactory<>("subcategoryProduct"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colBuyPrice.setCellValueFactory(new PropertyValueFactory<>("buyPrice"));
        colSalePrice.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        configurationTableView();
    }

    private void configurationTableView() {
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<TableColumn<Product, Void>, TableCell<Product, Void>>() {

            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<Product, Void>() {
                    private final Button btnEdit = buttonManager.createButton("btnEditIcon.png","yellow", 30, 30);
                    private final Button btnEliminate = buttonManager.createButton("btnDeleteIcon.png", "red", 30, 30);
                    {
                        btnEliminate.setOnAction((event) -> {
                            Product product = getTableView().getItems().get(getIndex());
                            productService.delete(product);
                        });
                        btnEdit.setOnAction((event) -> {
                            Product product = getTableView().getItems().get(getIndex());
                            try {
                                updateProductView(product);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Product product = getTableView().getItems().get(getIndex());
                            btnEliminate.setDisable(!product.isStatus());
                            if (product.getStock() == 0) {
                                btnEliminate.setDisable(false);
                                product.setStatus(false);
                            }else {
                                btnEliminate.setDisable(true);
                                product.setStatus(true);
                            }
                            btnEliminate.setDisable(currentUser.getRole().equals(Rol.SELLER));
                            btnEdit.setDisable(currentUser.getRole().equals(Rol.SELLER));

                            HBox buttons = new HBox(btnEdit, btnEliminate);
                            buttons.setAlignment(Pos.CENTER);
                            buttons.setSpacing(5);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);

        colStatus.setCellFactory(column -> new TableCell<Product,Boolean>(){
            @Override
            protected void updateItem(Boolean item,boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null){
                    setText("");
                }else {
                    setText(item ? "Disponible" : "No Disponible");
                    autosize();
                }
            }
        });
        colSalePrice.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    int newValue = (int) (item * 1.19);
                    setText(currencyFormat.format(newValue));
                    autosize();
                }
            }
        });
        colBuyPrice.setCellFactory(column -> new TableCell<Product, Integer>() {
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
    }

    private void managedUserView(User user){
        if(user.getRole().equals(Rol.SELLER)){
            btnAddCategories.setDisable(true);
            btnAddProduct.setDisable(true);
            btnSupplier.setDisable(true);
            btnPurchase.setDisable(true);
            colAction.setVisible(false);
        }
    }

    @FXML
    private void setupSearchFilter() {
        txtSearchProduct .textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                listAllProduct.setItems(list);
                listAllProduct.refresh();
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                ObservableList<Product> filteredList = FXCollections.observableArrayList();
                for (Product product : list) {
                    if (product.getBrand().getName().toLowerCase().contains(lowerCaseFilter) ||
                            product.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                            product.getCategory().getName().toLowerCase().contains(lowerCaseFilter) ||
                            product.getSubcategoryProduct().getName().toLowerCase().contains(lowerCaseFilter)) {
                        filteredList.add(product);
                    }
                }
                listAllProduct.setItems(filteredList);
            }
        });
    }
}