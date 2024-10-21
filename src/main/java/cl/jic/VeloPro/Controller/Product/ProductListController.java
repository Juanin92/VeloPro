package cl.jic.VeloPro.Controller.Product;

import cl.jic.VeloPro.Controller.Purchase.PurchaseController;
import cl.jic.VeloPro.Controller.Sale.SaleController;
import cl.jic.VeloPro.Model.DTO.DetailPurchaseDTO;
import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Entity.Product.SubcategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.UnitProduct;
import cl.jic.VeloPro.Model.Enum.StatusProduct;
import cl.jic.VeloPro.Service.Product.Interface.IProductService;
import cl.jic.VeloPro.Service.Purchase.Interfaces.IPurchaseDetailService;
import cl.jic.VeloPro.Service.Sale.Interfaces.ISaleDetailService;
import cl.jic.VeloPro.Utility.ButtonManager;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.Validation.ShowingStageValidation;
import cl.jic.VeloPro.VeloProApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@Component
public class ProductListController implements Initializable {

    @FXML private AnchorPane purchasePane;
    @FXML private TableView<Product> listSearchProduct;
    @FXML private TableColumn<Product, String> colBrandPurchase;
    @FXML private TableColumn<Product, CategoryProduct> colCategory;
    @FXML private TableColumn<Product, String> colDescriptionPurchase;
    @FXML private TableColumn<Product, Long> colIdProductPurchase;
    @FXML private TableColumn<Product, StatusProduct> colStatus;
    @FXML private TableColumn<Product, SubcategoryProduct> colSubcategory;
    @FXML private TableColumn<Product, UnitProduct> colUnit;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private CustomTextField txtSearchFastProduct;
    @FXML private Button btnAddProduct, btnAddCategories;

    @Autowired private IProductService productService;
    @Autowired private ISaleDetailService saleDetailService;
    @Autowired private IPurchaseDetailService purchaseDetailService;
    @Autowired private ShowingStageValidation stageValidation;
    @Autowired private NotificationManager notificationManager;
    @Autowired private ButtonManager buttonManager;
    @Autowired private PurchaseController purchaseController;
    @Autowired private SaleController saleController;

    @Setter private String view;
    private ObservableList<Product> productsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSearchFilterProduct();
    }

    @FXML
    private void handleButton(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnAddCategories)) {
            if (stageValidation.validateStage("Crear Categorías")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/categories.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/principalLogo.png"))));
            stage.setTitle("Crear Categorías");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            buttonManager.selectedButtonStage(btnAddCategories, scene, stage);
            stage.show();
        }
        if (event.getSource().equals(btnAddProduct)) {
            if (stageValidation.validateStage("Crear Categorías")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/products.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/principalLogo.png"))));
            stage.setTitle("Crear Categorías");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            buttonManager.selectedButtonStage(btnAddProduct, scene, stage);
            stage.show();
        }
    }

    public void loadDataProductSearchList(){
        productsList = FXCollections.observableArrayList(productService.getAll());
        if (view.equals("sale")) {
            productsList.removeIf(product -> !product.isStatus() || product.getSalePrice() == 0);
        }
        listSearchProduct.setItems(productsList);
        listSearchProduct.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listSearchProduct.getSelectionModel().selectedItemProperty().addListener((observableValue, product, newValue) -> {
            if (view.equals("purchase")){
                createDtoPurchase(newValue);
            }
            if (view.equals("sale")){
                createDtoSale(newValue);
            }
        });

        colIdProductPurchase.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBrandPurchase.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSubcategory.setCellValueFactory(new PropertyValueFactory<>("subcategoryProduct"));
        colDescriptionPurchase.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusProduct"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        configurationTableView();
    }

    private void createDtoPurchase(Product product){
        DetailPurchaseDTO dto = purchaseDetailService.createDTO(product);
        purchaseController.createDto(dto);
    }

    private void createDtoSale(Product product){
        DetailSaleDTO dto = saleDetailService.createDTO(product);
        saleController.createDto(dto);
    }

    private void configurationTableView() {
        colStatus.setCellFactory(column -> new TableCell<>(){
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

    public void loadData(){
        purchasePane.setVisible(view.equals("purchase"));
        colStatus.setVisible(view.equals("purchase"));
        colStock.setVisible(view.equals("sale"));
        loadDataProductSearchList();
    }
}