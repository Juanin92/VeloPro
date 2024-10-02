package cl.jic.VeloPro.Controller.Product;

import cl.jic.VeloPro.Model.Entity.Product.BrandProduct;
import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.SubcategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.UnitProduct;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Service.Product.Interface.IBrandService;
import cl.jic.VeloPro.Service.Product.Interface.ICategoryService;
import cl.jic.VeloPro.Service.Product.Interface.ISubcategoryService;
import cl.jic.VeloPro.Service.Product.Interface.IUnitService;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class CategoriesController implements Initializable {

    @FXML private Button btnSaveBrand, btnSaveCategory, btnSaveSubcategory, btnSaveUnit;
    @FXML private CustomTextField txtBrand, txtCategory, txtSubcategory, txtUnit;
    @FXML private ComboBox<CategoryProduct> cbCategories;
    @FXML private Label lblBrand, lblCategory, lblSubCategory, lblUnit;

    @Autowired private IBrandService brandService;
    @Autowired private IUnitService unitService;
    @Autowired private ICategoryService categoryService;
    @Autowired private ISubcategoryService subcategoryService;
    @Autowired private IRecordService recordService;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private NotificationManager notificationManager;
    @Autowired private Session session;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graphicsValidator.handleTextfieldChangeWithLabel(txtBrand,lblBrand);
        graphicsValidator.handleTextfieldChangeWithLabel(txtCategory,lblCategory);
        graphicsValidator.handleTextfieldChangeWithLabel(txtSubcategory,lblSubCategory);
        graphicsValidator.handleTextfieldChangeWithLabel(txtUnit,lblUnit);
        Platform.runLater(this::setupEnterKeyActions);
        Platform.runLater(this::loadComboBox);
    }

    @FXML
    public void addBrands(){
        try{
            BrandProduct brand = new BrandProduct();
            brand.setName(txtBrand.getText());
            brandService.save(brand);
            txtBrand.clear();
            notificationManager.successNotification("Registro Exitoso!", brand.getName() + ", Ha sido agregada al sistema.", Pos.CENTER);
            recordService.registerAction(session.getCurrentUser(), "CREATE", "Crear Marca" + brand.getName());
        }catch (IllegalArgumentException e){
            graphicsValidator.settingAndValidationTextField(txtBrand,true, e.getMessage());
        }
    }

    @FXML
    public void addUnit(){
        try{
            UnitProduct unit = new UnitProduct();
            unit.setNameUnit(txtUnit.getText());
            unitService.save(unit);
            txtUnit.clear();
            notificationManager.successNotification("Registro Exitoso!", unit.getNameUnit() + ", Ha sido agregada al sistema.", Pos.CENTER);
            recordService.registerAction(session.getCurrentUser(), "CREATE", "Crear unidad" + unit.getNameUnit());
        }catch (IllegalArgumentException e){
            graphicsValidator.settingAndValidationTextField(txtUnit,true, e.getMessage());
        }
    }

    @FXML
    public void addCategories(){
        try{
            CategoryProduct category = new CategoryProduct();
            category.setName(txtCategory.getText());
            categoryService.save(category);
            txtCategory.clear();
            notificationManager.successNotification("Registro Exitoso!", category.getName() + ", Ha sido agregada al sistema.", Pos.CENTER);
            recordService.registerAction(session.getCurrentUser(), "CREATE", "Crear categoría" + category.getName());
            loadComboBox();
        }catch (IllegalArgumentException e){
            graphicsValidator.settingAndValidationTextField(txtCategory,true, e.getMessage());
        }
    }

    @FXML
    public void addSubcategories(){
        try{
            CategoryProduct categoryProduct = cbCategories.getSelectionModel().getSelectedItem();
            SubcategoryProduct subcategory= new SubcategoryProduct();
            subcategory.setName(txtSubcategory.getText());
            subcategoryService.save(subcategory,cbCategories.getSelectionModel().getSelectedItem());
            txtSubcategory.clear();
            notificationManager.successNotification("Registro Exitoso!", subcategory.getName() + ", Ha sido agregada al sistema.", Pos.CENTER);
            recordService.registerAction(session.getCurrentUser(), "CREATE", "Crear Subcategoría" + subcategory.getName());
        }catch (IllegalArgumentException e){
            graphicsValidator.settingAndValidationTextField(txtSubcategory,true, e.getMessage());
        }
    }

    private void loadComboBox(){
        List<CategoryProduct> categoryProductList = categoryService.getall();
        for (CategoryProduct categoryProduct : categoryProductList) {
            cbCategories.getItems().add(categoryProduct);
        }
    }

    private void setupEnterKeyActions(){
        txtBrand.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addBrands();
            }
        });
        txtCategory.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addCategories();
            }
        });
        txtSubcategory.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addSubcategories();
            }
        });
        txtUnit.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addUnit();
            }
        });
    }
}