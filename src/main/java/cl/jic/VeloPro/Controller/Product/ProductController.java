package cl.jic.VeloPro.Controller.Product;

import cl.jic.VeloPro.Model.Entity.Product.*;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.MovementsType;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Service.Report.Interfaces.IKardexService;
import cl.jic.VeloPro.Service.Product.Interface.*;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Setter;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ProductController implements Initializable {

    @FXML private Button btnCancel, btnSaveProducts, btnUpdateProducts;
    @FXML private ComboBox<BrandProduct> cbBrand;
    @FXML private ComboBox<SubcategoryProduct> cbSubcategory;
    @FXML private ComboBox<UnitProduct> cbUnit;
    @FXML private ComboBox<CategoryProduct> cbCategories;
    @FXML private CustomTextField txtDescription,txtQuantity,txtSalePrice,txtComment;
    @FXML private StackPane stackPaneProducts;
    @FXML private AnchorPane panePrice, paneQuantity;
    @FXML private Label lblSalePrice, lblDescription, lblQuantity, lblBuyPrice;

    @Autowired private IBrandService brandService;
    @Autowired private IUnitService unitService;
    @Autowired private ICategoryService categoryService;
    @Autowired private ISubcategoryService subcategoryService;
    @Autowired private IProductService productService;
    @Autowired private IProductList productList;
    @Autowired private IKardexService kardexService;
    @Autowired private IRecordService recordService;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private NotificationManager notificationManager;
    @Autowired private Session session;

    @Setter private Product selectedProduct;
    @Setter private Button btnAddProduct;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        graphicsValidator.handleTextfieldChangeWithLabel(txtDescription,lblDescription);
        graphicsValidator.handleTextfieldChangeWithLabel(txtQuantity, lblQuantity);
        graphicsValidator.handleTextfieldChangeWithLabel(txtSalePrice, lblSalePrice);
        graphicsValidator.handleComboBoxChange(cbBrand);
        graphicsValidator.handleComboBoxChange(cbCategories);
        graphicsValidator.handleComboBoxChange(cbSubcategory);
        graphicsValidator.handleComboBoxChange(cbUnit);
        loadComboBox();
        btnCancel.setOnAction(event -> {
            Stage stage = (Stage) stackPaneProducts.getScene().getWindow();
            stage.close();
            selectedProduct = null;
            if (btnAddProduct != null){
                btnAddProduct.getStyleClass().remove("custom-button-selected");
            }
        });
        Platform.runLater(this::infoProduct);
    }

    @FXML
    private void addProduct(){
        try{
            Product product = new Product();
            product.setBrand(cbBrand.getValue());
            product.setCategory(cbCategories.getValue());
            product.setSubcategoryProduct(cbSubcategory.getValue());
            product.setUnit(cbUnit.getValue());
            txtDescription.setText(String.valueOf(cbBrand.getValue()) + cbCategories.getValue());
            product.setDescription(txtDescription.getText());
            productService.save(product);
            productList.loadDataStockList();
            notificationManager.successNotification("Registro Exitoso!", "El producto agregado al sistema correctamente.", Pos.CENTER );
            recordService.registerAction(currentUser, "CREATE", "Crear Producto" + product.getDescription());
        }catch (Exception e){
            handleValidationException(e.getMessage());
        }
    }

    @FXML
    private void updateProduct(){
        try{
            if (selectedProduct.isStatus()){
                selectedProduct.setBrand(cbBrand.getValue());
                selectedProduct.setDescription(txtDescription.getText());
                selectedProduct.setCategory(cbCategories.getValue());
                selectedProduct.setSubcategoryProduct(cbSubcategory.getValue());
                selectedProduct.setUnit(cbUnit.getValue());
                selectedProduct.setSalePrice(Integer.parseInt(txtSalePrice.getText()));
                selectedProduct.setStock(Integer.parseInt(txtQuantity.getText()));
                productService.update(selectedProduct);
                productList.loadDataStockList();
                if (txtComment.getText() != null){
                    kardexService.addKardex(selectedProduct, Integer.parseInt(txtQuantity.getText()), txtComment.getText(), MovementsType.AJUSTE, currentUser);
                }else {
                    kardexService.addKardex(selectedProduct, Integer.parseInt(txtQuantity.getText()), "Actualización Precio", MovementsType.AJUSTE, currentUser);
                }
                notificationManager.successNotification("Actualización Exitosa!", "El producto se ha actualizado en el sistema correctamente.", Pos.CENTER);
                recordService.registerAction(currentUser, "CHANGE", "Cambio Producto" + selectedProduct.getDescription());
            }else {
                System.out.println("Implementar la actualizacion cuando no esta disponible el producto");
            }
        }catch (Exception e){
            handleValidationException(e.getMessage());
        }
    }

    private void infoProduct(){
        if (selectedProduct != null){
            if (selectedProduct.isStatus()){
                elementsProductEdit();
            }else {
                elementsProductEdit();
                System.out.println("Integrar la opcion si el producto no esta disponible");
            }
            txtQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
                if (selectedProduct.getStock() != Integer.parseInt(txtQuantity.getText())){
                    txtComment.setVisible(true);
                }
            });
        } else {
            panePrice.setVisible(false);
            paneQuantity.setVisible(false);
            btnSaveProducts.setVisible(true);
            btnUpdateProducts.setVisible(false);
        }
    }

    private void elementsProductEdit(){
        if (currentUser.getRole().equals(Rol.MASTER) || currentUser.getRole().equals(Rol.WAREHOUSE)){
            cbBrand.setDisable(false);
            cbCategories.setDisable(false);
            cbSubcategory.setDisable(false);
            cbUnit.setDisable(false);
        }else {
            cbBrand.setDisable(true);
            cbCategories.setDisable(true);
            cbSubcategory.setDisable(true);
            cbUnit.setDisable(true);
        }
        cbBrand.setValue(selectedProduct.getBrand());
        cbCategories.setValue(selectedProduct.getCategory());
        cbSubcategory.setValue(selectedProduct.getSubcategoryProduct());
        cbUnit.setValue(selectedProduct.getUnit());
        txtDescription.setText(selectedProduct.getDescription());
        panePrice.setVisible(true);
        txtSalePrice.setText(String.valueOf(selectedProduct.getSalePrice()));
        lblBuyPrice.setText(String.format("$%,d", selectedProduct.getBuyPrice()));
        paneQuantity.setVisible(true);
        txtQuantity.setText(String.valueOf(selectedProduct.getStock()));
        btnSaveProducts.setVisible(false);
        btnUpdateProducts.setVisible(true);
    }

    private void loadComboBox(){
        List<CategoryProduct> categoryProductList = categoryService.getall();
        List<UnitProduct> unitProductList = unitService.getAll();
        List<SubcategoryProduct> subcategoryProductList = subcategoryService.getAll();
        List<BrandProduct> brandProductList = brandService.getAll();
        for (BrandProduct brandProduct : brandProductList){
            cbBrand.getItems().add(brandProduct);
        }
        for (UnitProduct unitProduct : unitProductList){
            cbUnit.getItems().add(unitProduct);
        }
        for (CategoryProduct categoryProduct : categoryProductList) {
            cbCategories.getItems().add(categoryProduct);
        }
        cbBrand.setOnAction(event -> txtDescription.setText(cbBrand.getValue().getName() + " "));
        cbCategories.setOnAction(event -> {
            CategoryProduct selectedCategory = cbCategories.getValue();
            if (selectedCategory != null) {
                List<SubcategoryProduct> relatedSubcategories = new ArrayList<>();
                for (SubcategoryProduct subcategory : subcategoryProductList) {
                    if (subcategory.getCategory().getName().equals(selectedCategory.getName())) {
                        relatedSubcategories.add(subcategory);
                    }
                }
                cbSubcategory.getItems().setAll(relatedSubcategories);
                cbSubcategory.setDisable(false);
            }
            txtDescription.setText(txtDescription.getText() + cbCategories.getValue().getName() + " ");
        });
    }

    private void handleValidationException(String errorMessage){
        switch (errorMessage){
            case "Ingrese una descripción":
                graphicsValidator.settingAndValidationTextField(txtDescription, true, errorMessage);
                break;
            case "Cantidad de ser mayor a 0":
                graphicsValidator.settingAndValidationTextField(txtQuantity, true, errorMessage);
                break;
            case "Precio de ser mayor a 0":
                graphicsValidator.settingAndValidationTextField(txtSalePrice, true, errorMessage);
                break;
            case "Seleccione una marca":
                graphicsValidator.settingAndValidationComboBox(cbBrand,true, errorMessage);
                break;
            case "Seleccione una categoría":
                graphicsValidator.settingAndValidationComboBox(cbCategories,true, errorMessage);
                break;
            case "Seleccione una subcategoría":
                graphicsValidator.settingAndValidationComboBox(cbSubcategory,true, errorMessage);
                break;
            case "Seleccione una unidad":
                graphicsValidator.settingAndValidationComboBox(cbUnit,true, errorMessage);
                break;
        }
    }
}