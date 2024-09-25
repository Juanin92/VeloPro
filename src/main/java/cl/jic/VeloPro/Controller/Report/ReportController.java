package cl.jic.VeloPro.Controller.Report;

import cl.jic.VeloPro.Controller.HomeController;
import cl.jic.VeloPro.Model.DTO.*;
import cl.jic.VeloPro.Service.Report.Interfaces.IReportService;
import cl.jic.VeloPro.Utility.ButtonManager;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ReportController implements Initializable {
    @FXML private BarChart<String, Number> barChart;
    @FXML private Button btnSaleCategory, btnSumSale, btnEarning, btnAverage, btnSaleTable;
    @FXML private Button btnSaleProduct, btnTotalSale, btnKardex, btnAccept;
    @FXML private DatePicker dateFrom, dateTo;
    @FXML private PieChart pieChart;
    @FXML private RadioButton rb30Days, rb60Days, rb6Months, rb90Days, rbYear;
    @FXML private Label lblMessage;
    @FXML private TableView<Object> tableRank;

    @Autowired private IReportService reportService;
    @Autowired private ButtonManager buttonManager;
    @Autowired private ShowingStageValidation stageValidation;

    @Setter private HomeController homeController;
    private LocalDate start = LocalDate.now();
    private String range = "";
    private int selectedButton = 0;
    private int totalSale = 0;
    private int saleDto = 0;
    private Button btnSelected;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadData();
    }

    @FXML
    private void handleButton(ActionEvent e) {
        if (e.getSource().equals(btnTotalSale)) {
            selectedButton = 1;
            activeElement();
            buttonManager.selectedButtonPane(btnTotalSale,btnSelected);
            btnSelected = btnTotalSale;
        } else if (e.getSource().equals(btnSumSale)) {
            selectedButton = 2;
            activeElement();
            buttonManager.selectedButtonPane(btnSumSale,btnSelected);
            btnSelected = btnSumSale;
        } else if (e.getSource().equals(btnAverage)) {
            selectedButton = 3;
            activeElement();
            buttonManager.selectedButtonPane(btnAverage,btnSelected);
            btnSelected = btnAverage;
        } else if (e.getSource().equals(btnEarning)) {
            selectedButton = 4;
            activeElement();
            buttonManager.selectedButtonPane(btnEarning,btnSelected);
            btnSelected = btnEarning;
        }else if (e.getSource().equals(btnSaleProduct)) {
            selectedButton = 5;
            activeElement();
            buttonManager.selectedButtonPane(btnSaleProduct,btnSelected);
            btnSelected = btnSaleProduct;
        }else if (e.getSource().equals(btnSaleCategory)) {
            selectedButton = 6;
            activeElement();
            buttonManager.selectedButtonPane(btnSaleCategory,btnSelected);
            btnSelected = btnSaleCategory;
        } else if (e.getSource().equals(btnAccept)) {
            if (dateFrom.getValue() == null && dateTo.getValue() == null){
                lblMessage.setText("Debe seleccionar 2 fechas válidas");
            }
            unselectRadioButtons();
            rangeDateMethods();
            dateFrom.setValue(null);
            dateTo.setValue(null);
            btnAccept.setDisable(true);
        }else {
            activeRadio(true);
        }
    }

    @FXML
    private void handleRegisterTable(ActionEvent e) throws IOException {
        if (e.getSource().equals(btnKardex)){
            if (stageValidation.validateStage("Kardex")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/ReportView/kardex.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Kardex");
            stage.initStyle(StageStyle.UNDECORATED);
            buttonManager.selectedButtonStage(btnKardex, scene, stage);
            stage.show();
        } else if (e.getSource().equals(btnSaleTable)) {
            if (stageValidation.validateStage("Registro de ventas")) return;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/ReportView/saleRegister.fxml"));
            fxmlLoader.setControllerFactory(VeloProApplication.getContext()::getBean);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Registro de ventas");
            stage.initStyle(StageStyle.UNDECORATED);
            buttonManager.selectedButtonStage(btnSaleTable, scene, stage);
            stage.show();
        }
    }

    private void loadData(){
        ToggleGroup radioGroup = new ToggleGroup();
        rb30Days.setToggleGroup(radioGroup);
        rb60Days.setToggleGroup(radioGroup);
        rb90Days.setToggleGroup(radioGroup);
        rb6Months.setToggleGroup(radioGroup);
        rbYear.setToggleGroup(radioGroup);

        rb30Days.setOnAction(this::selectedActionButton);
        rb60Days.setOnAction(this::selectedActionButton);
        rb90Days.setOnAction(this::selectedActionButton);
        rb6Months.setOnAction(this::selectedActionButton);
        rbYear.setOnAction(this::selectedActionButton);

        dateFrom.setOnAction(event -> {
            btnAccept.setDisable(false);
        });
        dateTo.setOnAction(event -> {
            btnAccept.setDisable(false);
        });
    }

    private void calculateTotalSale(){
        saleDto = 0;
        totalSale = 0;
        try{
            LocalDate end = calculateEndDate();
            lblMessage.setText(" " + end + " - " + start);

            List<DailySaleCountDTO> dailySale = reportService.getDailySale(start,end);
            Map<YearMonth, Double> saleByMonth = dailySale.stream()
                    .collect(Collectors.groupingBy(
                            sale -> YearMonth.from(sale.getDate()),
                            Collectors.summingDouble(DailySaleCountDTO::getSale)
                    ));
            Map<Year, Double> saleByYear = dailySale.stream()
                    .collect(Collectors.groupingBy(
                            sale -> Year.from(sale.getDate()),
                            Collectors.summingDouble(DailySaleCountDTO::getSale)
                    ));

            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            if (rb30Days.isSelected() || range.equals("daily")){
                System.out.println("LISTA: "+dailySale);
                System.out.println("30 dias");
                for (DailySaleCountDTO data : dailySale) {
                    String date = String.valueOf(data.getDate());
                    Number sales = data.getSale();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(date, sales);
                    Label label = new Label(sales.toString());
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    saleDto = (int) sales;
                    totalSale += saleDto;
                }
            }
            if (rb90Days.isSelected() || rb6Months.isSelected() || rb60Days.isSelected() || rbYear.isSelected() || range.equals("monthly")){
                System.out.println("LISTA: "+dailySale);
                System.out.println("60 dias");
                for (Map.Entry<YearMonth, Double> entry : saleByMonth.entrySet()) {
                    String month = entry.getKey().format(DateTimeFormatter.ofPattern("MMM"));
                    int sales = entry.getValue().intValue();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(month, sales);
                    Label label = new Label(String.valueOf(sales));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    saleDto = (int) sales;
                    totalSale += saleDto;
                }
            }
            if (range.equals("annual")){
                for (Map.Entry<Year, Double> entry : saleByYear.entrySet()) {
                    String year = entry.getKey().format(DateTimeFormatter.ofPattern("yyyy"));
                    int sales = entry.getValue().intValue();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(year, sales);
                    Label label = new Label(String.valueOf(sales));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    saleDto = (int) sales;
                    totalSale += saleDto;
                }
            }
            barChart.setTitle("Total de venta : " + totalSale);
            barChart.getData().add(series);
            populatePieChart(start,end);
        }catch (Exception e){
            lblMessage.setText(e.getMessage());
        }
    }

    private void calculateAverageSale(){
        try{
            totalSale = 0;
            LocalDate end = calculateEndDate();
            double avgValue;
            lblMessage.setText(" " + end + " - " + start);

            List<DailySaleAvgDTO> dailySaleAvg = reportService.getAverageTotalSaleDaily(start,end);
            List<Double> Averages = new ArrayList<>();
            Map<YearMonth, Double> saleByMonth = dailySaleAvg.stream()
                    .collect(Collectors.groupingBy(
                            sale -> YearMonth.from(sale.getDate()),
                            Collectors.summingDouble(DailySaleAvgDTO::getAvg)
                    ));
            Map<Year, Double> saleByYear = dailySaleAvg.stream()
                    .collect(Collectors.groupingBy(
                            sale -> Year.from(sale.getDate()),
                            Collectors.summingDouble(DailySaleAvgDTO::getAvg)
                    ));

            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            if (rb30Days.isSelected() || range.equals("daily")){
                for (DailySaleAvgDTO data : dailySaleAvg) {
                    String date = String.valueOf(data.getDate());
                    Number avg = data.getAvg();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(date, avg);
                    Label label = new Label(String.valueOf(String.format("$%,d", avg)));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    avgValue = data.getAvg();
                    totalSale += (int) avgValue;
                    Averages.add(avgValue);
                }
            }
            if (rb90Days.isSelected() || rb6Months.isSelected() || rb60Days.isSelected() || rbYear.isSelected() || range.equals("monthly")){
                for (Map.Entry<YearMonth, Double> entry : saleByMonth.entrySet()) {
                    String month = entry.getKey().format(DateTimeFormatter.ofPattern("MMM"));
                    int avg = entry.getValue().intValue();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(month, avg);
                    Label label = new Label(String.valueOf(String.format("$%,d", avg)));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    avgValue = entry.getValue();
                    totalSale += (int) avgValue;
                    Averages.add(avgValue);
                }
            }
            if (range.equals("annual")){
                for (Map.Entry<Year, Double> entry : saleByYear.entrySet()) {
                    String year = entry.getKey().format(DateTimeFormatter.ofPattern("yyyy"));
                    int avg = entry.getValue().intValue();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(year, avg);
                    Label label = new Label(String.valueOf(String.format("$%,d", avg)));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    avgValue = entry.getValue();
                    totalSale += (int) avgValue;
                    Averages.add(avgValue);
                }
            }
            int prom = totalSale / Averages.size();
            barChart.setTitle("Monto promedio de venta: " + String.format("$%,d", prom));
            barChart.getData().add(series);
        }catch (Exception e){
            lblMessage.setText(e.getMessage());
        }
    }

    private void calculateEarningSale(){
        try{
            LocalDate end = calculateEndDate();
            int totalProfit = 0;
            saleDto = 0;
            totalSale = 0;
            lblMessage.setText(" " + end + " - " + start);

            List<DailySaleEarningDTO> dailySaleEarning = reportService.getEarningSale(start,end);
            Map<YearMonth, Double> saleByMonth = dailySaleEarning.stream()
                    .collect(Collectors.groupingBy(
                            sale -> YearMonth.from(sale.getSaleDate()),
                            Collectors.summingDouble(DailySaleEarningDTO::getProfit)
                    ));
            Map<Year, Double> saleByYear = dailySaleEarning.stream()
                    .collect(Collectors.groupingBy(
                            sale -> Year.from(sale.getSaleDate()),
                            Collectors.summingDouble(DailySaleEarningDTO::getProfit)
                    ));

            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            if (rb30Days.isSelected() || range.equals("daily")){
                for (DailySaleEarningDTO data : dailySaleEarning) {
                    String date = String.valueOf(data.getSaleDate());
                    int profit = data.getProfit();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(date, profit);
                    Label label = new Label(String.valueOf(String.format("$%,d", profit)));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    saleDto = (int) profit;
                    totalProfit += saleDto;
                }
            }
            if (rb90Days.isSelected() || rb6Months.isSelected() || rb60Days.isSelected() || rbYear.isSelected() || range.equals("monthly")){
                for (Map.Entry<YearMonth, Double> entry : saleByMonth.entrySet()) {
                    String month = entry.getKey().format(DateTimeFormatter.ofPattern("MMM"));
                    int profit = entry.getValue().intValue();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(month, profit);
                    Label label = new Label(String.valueOf(String.format("$%,d", profit)));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    saleDto = (int) profit;
                    totalProfit += saleDto;
                }
            }
            if (range.equals("annual")){
                for (Map.Entry<Year, Double> entry : saleByYear.entrySet()) {
                    String year = entry.getKey().format(DateTimeFormatter.ofPattern("yyyy"));
                    int profit = entry.getValue().intValue();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(year, profit);
                    Label label = new Label(String.valueOf(String.format("$%,d", profit)));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    saleDto = (int) profit;
                    totalProfit += saleDto;
                }
            }
            barChart.setTitle("Total de venta: " + String.format("$%,d", totalProfit));
            barChart.getData().add(series);
        }catch (Exception e){
            lblMessage.setText(e.getMessage());
        }
    }

    private void calculateSumSale(){
        try{
            totalSale = 0;
            LocalDate end = calculateEndDate();
            lblMessage.setText(" " + end + " - " + start);

            List<DailySaleSumDTO> dailySaleSum = reportService.getTotalSaleDaily(start,end);
            Map<YearMonth, Double> saleByMonth = dailySaleSum.stream()
                    .collect(Collectors.groupingBy(
                            sale -> YearMonth.from(sale.getDate()),
                            Collectors.summingDouble(DailySaleSumDTO::getSum)
                    ));
            Map<Year, Double> saleByYear = dailySaleSum.stream()
                    .collect(Collectors.groupingBy(
                            sale -> Year.from(sale.getDate()),
                            Collectors.summingDouble(DailySaleSumDTO::getSum)
                    ));

            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            if (rb30Days.isSelected() || range.equals("daily")){
                for (DailySaleSumDTO data : dailySaleSum) {
                    String date = String.valueOf(data.getDate());
                    Number sum = data.getSum();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(date, sum);
                    Label label = new Label(String.valueOf(String.format("$%,d", sum)));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    saleDto = (int) sum;
                    totalSale += saleDto;
                }
            }
            if (rb90Days.isSelected() || rb6Months.isSelected() || rb60Days.isSelected() || rbYear.isSelected() || range.equals("monthly")){
                for (Map.Entry<YearMonth, Double> entry : saleByMonth.entrySet()) {
                    String month = entry.getKey().format(DateTimeFormatter.ofPattern("MMM"));
                    int sum = entry.getValue().intValue();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(month, sum);
                    Label label = new Label(String.valueOf(String.format("$%,d", sum)));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    saleDto = (int) sum;
                    totalSale += saleDto;
                }
            }
            if (range.equals("annual")){
                for (Map.Entry<Year, Double> entry : saleByYear.entrySet()) {
                    String year = entry.getKey().format(DateTimeFormatter.ofPattern("yyyy"));
                    int sum = entry.getValue().intValue();

                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(year, sum);
                    Label label = new Label(String.valueOf(String.format("$%,d", sum)));
                    label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                    label.setTranslateY(-35);

                    chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            StackPane stackPane = (StackPane) newNode;
                            stackPane.getChildren().add(label);
                        }
                    });
                    series.getData().add(chartData);
                    saleDto = (int) sum;
                    totalSale += saleDto;
                }
            }
            barChart.setTitle("Total de venta: " + String.format("$%,d", totalSale));
            barChart.getData().add(series);
            populatePieChart(start,end);
        }catch (Exception e){
            lblMessage.setText(e.getMessage());
        }
    }

    private void calculateProductSale(){
        try{
            LocalDate end = calculateEndDate();
            lblMessage.setText(" " + end + " - " + start);

            List<ProductSaleDTO> mostSoldProduct = reportService.getMostProductSale(start, end);
            barChart.getData().clear();

            Map<String, Integer> brandTotalMap = new HashMap<>();
            for (ProductSaleDTO data : mostSoldProduct) {
                String brand = data.getBrand();
                int total = data.getTotal();
                brandTotalMap.put(brand, brandTotalMap.getOrDefault(brand, 0) + total);
            }
            List<Map.Entry<String, Integer>> sortedEntries = brandTotalMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .toList();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                String brand = entry.getKey();
                int total = entry.getValue();

                XYChart.Data<String, Number> chartData = new XYChart.Data<>(brand, total);
                Label label = new Label(String.valueOf(total));
                label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                label.setTranslateY(-35);
                chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        StackPane stackPane = (StackPane) newNode;
                        stackPane.getChildren().add(label);
                    }
                });
                series.getData().add(chartData);
            }
            barChart.setTitle("Los 10 productos más vendidos");
            barChart.getData().add(series);
            tableRank.setVisible(true);
            configureTableView(mostSoldProduct, "description");
        } catch (Exception e) {
            lblMessage.setText(e.getMessage());
        }
    }

    private void calculateCategorySale(){
        try{
            LocalDate end = calculateEndDate();
            lblMessage.setText(" " + end + " - " + start);

            List<CategoriesSaleDTO> mostSoldCategory = reportService.getMostCategorySale(start,end);
            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (CategoriesSaleDTO data : mostSoldCategory) {
                String name = String.valueOf(data.getName());
                Number total = data.getTotal();

                XYChart.Data<String, Number> chartData = new XYChart.Data<>(name, total);
                Label label = new Label(String.valueOf(data.getTotal()));
                label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
                label.setTranslateY(-35);
                chartData.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        StackPane stackPane = (StackPane) newNode;
                        stackPane.getChildren().add(label);
                    }
                });
                series.getData().add(chartData);
            }
            barChart.setTitle("Las 10 categorías más vendidas");
            barChart.getData().add(series);
            tableRank.setVisible(true);
            configureTableView(mostSoldCategory,"name");
        }catch (Exception e){
            lblMessage.setText(e.getMessage());
        }
    }

    private void rangeDateMethods(){
        if (selectedButton == 1){
            calculateTotalSale();
        } else if (selectedButton == 2) {
            calculateSumSale();
        } else if (selectedButton == 3) {
            calculateAverageSale();
        }else if (selectedButton == 4) {
            calculateEarningSale();
        }else if (selectedButton == 5) {
            calculateProductSale();
        }else if (selectedButton == 6) {
            calculateCategorySale();
        }
    }

    private void populatePieChart(LocalDate start, LocalDate end) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        List<DailySaleCountDTO> dailySaleList = reportService.getDailySale(start, end);
        Map<String, Double> saleByPeriod;

        if (rb30Days.isSelected() || range.equals("daily")) {
            saleByPeriod = dailySaleList.stream()
                    .collect(Collectors.groupingBy(
                            sale -> sale.getDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                            Collectors.summingDouble(DailySaleCountDTO::getSale)
                    ));
        } else {
            saleByPeriod = dailySaleList.stream()
                    .collect(Collectors.groupingBy(
                            sale -> {
                                if (range.equals("annual")) {
                                    return sale.getDate().format(DateTimeFormatter.ofPattern("yyyy"));
                                } else {
                                    return sale.getDate().format(DateTimeFormatter.ofPattern("MMM"));
                                }
                            },
                            Collectors.summingDouble(DailySaleCountDTO::getSale)
                    ));
        }

        for (Map.Entry<String, Double> entry : saleByPeriod.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / saleByPeriod.values().stream().mapToDouble(value -> value).sum();
            PieChart.Data data = new PieChart.Data(entry.getKey() + " (" + String.format("%.1f%%", percentage) + ")", percentage);
            pieChartData.add(data);
        }
        pieChart.setData(pieChartData);
    }

    private LocalDate calculateEndDate() {
        start = LocalDate.now();
        range = "";
        if (rb30Days.isSelected()) {
            return start.minusMonths(1);
        } else if (rb60Days.isSelected()) {
            return start.minusMonths(2);
        } else if (rb90Days.isSelected()) {
            return start.minusMonths(3);
        } else if (rb6Months.isSelected()) {
            return start.minusMonths(6);
        } else if (rbYear.isSelected()) {
            return start.minusYears(1);
        } else if (!(dateTo.getValue() == null) && !(dateFrom.getValue() == null)) {
            start = dateTo.getValue();
            LocalDate end = dateFrom.getValue();
            range = determinateRangeDate(start, end);
            System.out.println("rango: "+range);
            return end;
        }
        return null;
    }

    private void configureTableView(List<?> data, String nameColumn) {
        TableColumn<Object, String> colName = new TableColumn<>("Nombre");
        TableColumn<Object, Number> colTotal = new TableColumn<>("Total");
        colName.setCellValueFactory(new PropertyValueFactory<>(nameColumn));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colName.setPrefWidth(110);
        colTotal.setPrefWidth(20);

        colName.setCellFactory(column -> new TableCell<Object, String>() {
            private final Text text = new Text();
            {
                setGraphic(text);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    text.setText(item);
                    setText(null);
                    setGraphic(text);
                }
            }
        });
        colTotal.setCellFactory(column -> new TableCell<Object, Number>() {
            private final Text text = new Text();
            {
                setGraphic(text);
            }

            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    text.setText(item.toString());
                    setText(null);
                    setGraphic(text);
                }
            }
        });

        tableRank.getColumns().clear();
        tableRank.getColumns().addAll(colName, colTotal);
        ObservableList<Object> observableData = FXCollections.observableArrayList(data);
        tableRank.setItems(observableData);

        double rowHeight = 25.5;
        int numRows = Math.min(observableData.size()+1, 10);
        tableRank.setFixedCellSize(rowHeight);
        double tableHeight = rowHeight * numRows;
        tableRank.setPrefHeight(tableHeight);
    }

    private void selectedActionButton(ActionEvent event){
        if (selectedButton == 1) {
            calculateTotalSale();
        } else if (selectedButton == 2){
            calculateSumSale();
        } else if (selectedButton == 3) {
            calculateAverageSale();
        }else if (selectedButton == 4) {
            calculateEarningSale();
        }else if (selectedButton == 5) {
            calculateProductSale();
        }else if (selectedButton == 6) {
            calculateCategorySale();
        }
    }

    private String determinateRangeDate(LocalDate start, LocalDate end){
        long days = ChronoUnit.DAYS.between(end,start);
        if (days <= 31 ){
            return "daily";
        } else if (days > 60 && days <= 365) {
            return "monthly";
        } else {
            return "annual";
        }
    }

    private void unselectRadioButtons() {
        rb30Days.setSelected(false);
        rb60Days.setSelected(false);
        rb90Days.setSelected(false);
        rb6Months.setSelected(false);
        rbYear.setSelected(false);
    }

    private void activeElement(){
        activeRadio(false);
        barChart.getData().clear();
        pieChart.getData().clear();
        tableRank.setVisible(false);
        unselectRadioButtons();
    }

    private void activeRadio(boolean status){
        rb30Days.setDisable(status);
        rb60Days.setDisable(status);
        rb90Days.setDisable(status);
        rb6Months.setDisable(status);
        rbYear.setDisable(status);
        dateFrom.setDisable(status);
        dateTo.setDisable(status);
    }
}