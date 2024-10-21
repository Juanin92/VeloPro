package cl.jic.VeloPro.Controller;

import cl.jic.VeloPro.Controller.Costumer.CostumerController;
import cl.jic.VeloPro.Controller.Product.StockController;
import cl.jic.VeloPro.Controller.Report.ReportController;
import cl.jic.VeloPro.Controller.Sale.CashRegisterController;
import cl.jic.VeloPro.Controller.Sale.SaleController;
import cl.jic.VeloPro.Model.Entity.LocalData;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Service.User.ILocalDataService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.VeloProApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@Component
public class HomeController implements Initializable {

    @FXML private Button btnCostumer, btnExit, btnReport, btnSale, btnSetting, btnStock, btnLogo, btnRecord;
    @FXML private Label lblTimeLocal, lblDateLocal;
    @FXML private StackPane homeView;
    @FXML private AnchorPane paneHome, paneBtnStock, paneBtnSale, paneBtnSetting, paneBtnCostumer, paneBtnReport, paneBtnRecord;
    @FXML private ImageView imgLogo;

    @Autowired private IRecordService recordService;
    @Autowired private ILocalDataService localDataService;
    @Autowired private Session session;
    @Autowired private NotificationManager notifications;
    @Setter private boolean activeToken;
    private User currentUser;
    private boolean out = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        getTime();
        backgroundSetup();
        managedUserView(currentUser);
        showCashRegisterOpeningForm();
        homeView.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnCloseRequest(event -> {
                            event.consume();
                            handleClosingRegisterView();
                        });
                    }
                });
            }
        });
    }

    @FXML
    public void handleButton(ActionEvent event) throws IOException {
        if (event.getSource().equals(btnSale)) {
            if (!homeView.getChildren().contains(homeView.lookup("#saleView"))) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/Sales/sales.fxml"));
                fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
                Parent root = fxmlLoader.load();
                SaleController saleController = fxmlLoader.getController();
                saleController.setHomeController(this);
                saleController.setViewSelected(true);
                homeView.getChildren().setAll(root);
            }
            changeColorMenu(paneBtnSale,btnSale);
        } else if (event.getSource().equals(btnStock)) {
            if (!homeView.getChildren().contains(homeView.lookup("#stockView"))) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/LogisticView/stock.fxml"));
                fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
                Parent root = fxmlLoader.load();
                StockController stockController = fxmlLoader.getController();
                stockController.setHomeController(this);
                homeView.getChildren().setAll(root);
            }
            changeColorMenu(paneBtnStock,btnStock);
        } else if (event.getSource().equals(btnCostumer)) {
            if (!homeView.getChildren().contains(homeView.lookup("#costumerView"))) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/CostumerView/costumer.fxml"));
                fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
                Parent root = fxmlLoader.load();
                CostumerController costumerController = fxmlLoader.getController();
                costumerController.setHomeController(this);
                homeView.getChildren().setAll(root);
            }
            changeColorMenu(paneBtnCostumer,btnCostumer);
        }  else if (event.getSource().equals(btnReport)) {
            if (!homeView.getChildren().contains(homeView.lookup("#reportView"))) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/ReportView/report.fxml"));
                fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
                Parent root = fxmlLoader.load();
                ReportController reportController = fxmlLoader.getController();
                reportController.setHomeController(this);
                homeView.getChildren().setAll(root);
            }
            changeColorMenu(paneBtnReport,btnReport);
        } else if (event.getSource().equals(btnSetting)) {
            if (!homeView.getChildren().contains(homeView.lookup("#settingView"))) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/setting.fxml"));
                fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
                Parent root = fxmlLoader.load();
                SettingController settingController = fxmlLoader.getController();
                settingController.setHomeController(this);
                settingController.setActiveToken(activeToken);
                homeView.getChildren().setAll(root);
            }
            changeColorMenu(paneBtnSetting,btnSetting);
        } else if (event.getSource().equals(btnLogo)) {
            if (homeView.getChildren() != null) {
                homeView.getChildren().clear();
            }
            homeView.getChildren().add(paneHome);
            changeColorMenu(null,btnLogo);
        }else if (event.getSource().equals(btnRecord)) {
            if (!homeView.getChildren().contains(homeView.lookup("#recordView"))) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/record.fxml"));
                fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
                Parent root = fxmlLoader.load();
                homeView.getChildren().setAll(root);
            }
            changeColorMenu(paneBtnRecord,btnRecord);
        }else if (event.getSource().equals(btnExit)) {
            handleClosingRegisterView();
        }
    }

    private void showCashRegisterOpeningForm() {
        if(!currentUser.getRole().equals(Rol.WAREHOUSE)){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/Sales/openingCashRegister.fxml"));
                fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
                Parent root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/principalLogo.png"))));
                stage.setTitle("Registro Apertura Caja");
                stage.setResizable(false);
                if (currentUser.getRole().equals(Rol.MASTER)){
                    stage.initModality(Modality.APPLICATION_MODAL);
                } else{
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setOnCloseRequest(events -> {
                        btnSale.setVisible(false);
                        btnCostumer.setVisible(false);
                        btnStock.setVisible(false);
                        btnReport.setVisible(false);
                        btnSetting.setVisible(false);
                        btnLogo.setDisable(true);
                        out = true;
                    });
                }
                stage.showAndWait();
            } catch (IOException e) {
                notifications.errorNotification("Error de Sistema", "No se ha podido realizar la acción", Pos.TOP_CENTER);
            }
        }
    }

    private void handleClosingRegisterView() {
        if (out || currentUser.getRole().equals(Rol.WAREHOUSE)){
            Stage stage = (Stage) homeView.getScene().getWindow();
            stage.close();
            recordService.registerEnd(currentUser);
        }else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/Sales/closingCashRegister.fxml"));
                fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
                Parent root = fxmlLoader.load();
                CashRegisterController cashRegisterController = fxmlLoader.getController();
                Stage currentStage = (Stage) homeView.getScene().getWindow();
                cashRegisterController.setHomeView(currentStage);

                Stage stage = new Stage();
                stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Images/principalLogo.png"))));
                stage.setTitle("Registro Cierre Caja");
                stage.setResizable(false);
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();

                if (currentUser.getRole().equals(Rol.MASTER)) {
                    currentStage.close();
                }
                recordService.registerEnd(currentUser);
            }catch (IOException e) {
                notifications.errorNotification("Error de Sistema", "No se ha podido realizar la acción", Pos.TOP_CENTER);
            }
        }
    }

    private void managedUserView(User user){
        if(user.getRole().equals(Rol.SELLER)){
            btnReport.setDisable(true);
        } else if (user.getRole().equals(Rol.WAREHOUSE)){
            btnSale.setDisable(true);
            btnCostumer.setDisable(true);
        } else if (user.getRole().equals(Rol.GUEST)){
            btnSetting.setDisable(true);
            btnReport.setDisable(true);
        } else if (user.getRole().equals(Rol.MASTER)) {
            btnRecord.setVisible(true);
        }
    }

    private void getTime(){
        LocalDateTime local = LocalDateTime.now();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        lblDateLocal.setText(date.format(local));
        lblTimeLocal.setText(time.format(local));
        List<LocalData> list = localDataService.getData();
        btnLogo.setText(list.getFirst().getName());
    }

    private void backgroundSetup(){
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        String style = "paneHome" + month;
        if (month == 9 && day >= 18 && day <= 30){
            style += "Special";
        }
        paneHome.getStyleClass().clear();
        paneHome.getStyleClass().add(style);
    }

    private void changeColorMenu(AnchorPane clickedPane, Button clickedBtn) {
        for (AnchorPane pane : new AnchorPane[]{paneBtnSale, paneBtnStock, paneBtnCostumer, paneBtnReport, paneBtnSetting, paneBtnRecord}) {
            if (pane != clickedPane) {
                pane.setStyle("-fx-background-color: #090e11;");
            }
        }
        for (Button btn : new Button[]{btnSale, btnStock, btnCostumer, btnReport, btnExit, btnSetting, btnRecord}) {
            if (btn != clickedBtn) {
                btn.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
            }
        }
        if (clickedBtn == btnLogo) {
            for (AnchorPane pane : new AnchorPane[]{paneBtnSale, paneBtnStock, paneBtnCostumer, paneBtnReport, paneBtnSetting, paneBtnRecord}) {
                pane.setStyle("-fx-background-color: #090e11;");
            }
        } else {
            clickedPane.setStyle("-fx-background-color: #3A6073;");
            clickedBtn.setStyle("-fx-text-fill: white; -fx-background-color: transparent;");
        }
    }

    public void redirectToHome() {
        if (homeView.getChildren() != null) {
            homeView.getChildren().clear();
        }
        homeView.getChildren().add(paneHome);
        changeColorMenu(null, btnLogo);
    }
}