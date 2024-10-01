package cl.jic.VeloPro.Controller.Report;

import cl.jic.VeloPro.Controller.HomeController;
import cl.jic.VeloPro.Model.DTO.*;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Report.Interfaces.IReportService;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
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
    @FXML private TableColumn<Object, Object> colA;
    @FXML private TableColumn<Object, Object> colB;

    @Autowired private IReportService reportService;
    @Autowired private ButtonManager buttonManager;
    @Autowired private ShowingStageValidation stageValidation;
    @Autowired private Session session;

    @Setter private HomeController homeController;
    private LocalDate start = LocalDate.now();
    private String range = "";
    private int selectedButton = 0;
    private int totalSale = 0;
    private int saleDto = 0;
    private Button btnSelected;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User currentUser = session.getCurrentUser();
        btnSaleTable.setVisible(!currentUser.getRole().equals(Rol.WAREHOUSE));
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
            ObservableList<Object> list = FXCollections.observableArrayList();
            Map<YearMonth, Double> saleByMonth = calculateByMonth(dailySale, dailySaleCountDTO -> (double) dailySaleCountDTO.getSale(), DailySaleCountDTO::getDate);
            Map<Year, Double> saleByYear = calculateByYear(dailySale, dailySaleCountDTO -> (double) dailySaleCountDTO.getSale(), DailySaleCountDTO::getDate);

            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            if (rb30Days.isSelected() || range.equals("daily")){
                for (DailySaleCountDTO data : dailySale) {
                    String date = String.valueOf(data.getDate());
                    Number sales = data.getSale();
                    list.add(data);
                    addDataToChart(series, date, sales, "quantity");
                    saleDto = (int) sales;
                    totalSale += saleDto;
                }
            }
            if (rb90Days.isSelected() || rb6Months.isSelected() || rb60Days.isSelected() || rbYear.isSelected() || range.equals("monthly")){
                for (Map.Entry<YearMonth, Double> entry : saleByMonth.entrySet()) {
                    String month = entry.getKey().format(DateTimeFormatter.ofPattern("MMM"));
                    LocalDate date = entry.getKey().atDay(1);
                    int sales = entry.getValue().intValue();
                    list.add(new DailySaleCountDTO(date,Long.parseLong(String.valueOf(sales))));

                    addDataToChart(series, month, sales , "quantity");
                    saleDto = (int) sales;
                    totalSale += saleDto;
                }
            }
            if (range.equals("annual")){
                for (Map.Entry<Year, Double> entry : saleByYear.entrySet()) {
                    String year = entry.getKey().format(DateTimeFormatter.ofPattern("yyyy"));
                    LocalDate date = entry.getKey().atDay(1);
                    int sales = entry.getValue().intValue();
                    list.add(new DailySaleCountDTO(date,Long.parseLong(String.valueOf(sales))));

                    addDataToChart(series, year, sales, "quantity");
                    saleDto = (int) sales;
                    totalSale += saleDto;
                }
            }
            barChart.setTitle("Total de venta : " + totalSale);
            barChart.getData().add(series);
            loadTableRank(list, "date", "sale", "Fecha", "N° Ventas");
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
            ObservableList<Object> list = FXCollections.observableArrayList();
            List<Double> Averages = new ArrayList<>();
            Map<YearMonth, Double> saleByMonth = calculateByMonth(dailySaleAvg, dailySaleAvgDTO -> (double) dailySaleAvgDTO.getAvg(), DailySaleAvgDTO::getDate);
            Map<Year, Double> saleByYear = calculateByYear(dailySaleAvg, dailySaleAvgDTO -> (double) dailySaleAvgDTO.getAvg(), DailySaleAvgDTO::getDate);

            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            if (rb30Days.isSelected() || range.equals("daily")){
                for (DailySaleAvgDTO data : dailySaleAvg) {
                    String date = String.valueOf(data.getDate());
                    Number avg = data.getAvg();
                    list.add(data);

                    addDataToChart(series, date, avg, "price");
                    avgValue = data.getAvg();
                    totalSale += (int) avgValue;
                    Averages.add(avgValue);
                }
            }
            if (rb90Days.isSelected() || rb6Months.isSelected() || rb60Days.isSelected() || rbYear.isSelected() || range.equals("monthly")){
                for (Map.Entry<YearMonth, Double> entry : saleByMonth.entrySet()) {
                    String month = entry.getKey().format(DateTimeFormatter.ofPattern("MMM"));
                    int avg = entry.getValue().intValue();
                    LocalDate date = entry.getKey().atDay(1);
                    list.add(new DailySaleAvgDTO(date, BigDecimal.valueOf(avg)));

                    addDataToChart(series, month, avg, "price");
                    avgValue = entry.getValue();
                    totalSale += (int) avgValue;
                    Averages.add(avgValue);
                }
            }
            if (range.equals("annual")){
                for (Map.Entry<Year, Double> entry : saleByYear.entrySet()) {
                    String year = entry.getKey().format(DateTimeFormatter.ofPattern("yyyy"));
                    int avg = entry.getValue().intValue();
                    LocalDate date = entry.getKey().atDay(1);
                    list.add(new DailySaleAvgDTO(date, BigDecimal.valueOf(avg)));

                    addDataToChart(series, year, avg, "price");
                    avgValue = entry.getValue();
                    totalSale += (int) avgValue;
                    Averages.add(avgValue);
                }
            }
            int prom = totalSale / Averages.size();
            barChart.setTitle("Monto promedio de venta: " + String.format("$%,d", prom));
            barChart.getData().add(series);
            loadTableRank(list, "date", "avg", "Fecha", "Promedio Venta");
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
            ObservableList<Object> list = FXCollections.observableArrayList();
            Map<YearMonth, Double> saleByMonth = calculateByMonth(dailySaleEarning, dailySaleEarningDTO -> (double) dailySaleEarningDTO.getProfit(), DailySaleEarningDTO::getSaleDate);
            Map<Year, Double> saleByYear = calculateByYear(dailySaleEarning, dailySaleEarningDTO -> (double) dailySaleEarningDTO.getProfit(), DailySaleEarningDTO::getSaleDate);

            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            if (rb30Days.isSelected() || range.equals("daily")){
                for (DailySaleEarningDTO data : dailySaleEarning) {
                    String date = String.valueOf(data.getSaleDate());
                    int profit = data.getProfit();
                    list.add(data);

                    addDataToChart(series, date, profit, "price");
                    saleDto = (int) profit;
                    totalProfit += saleDto;
                }
            }
            if (rb90Days.isSelected() || rb6Months.isSelected() || rb60Days.isSelected() || rbYear.isSelected() || range.equals("monthly")){
                for (Map.Entry<YearMonth, Double> entry : saleByMonth.entrySet()) {
                    String month = entry.getKey().format(DateTimeFormatter.ofPattern("MMM"));
                    int profit = entry.getValue().intValue();
                    LocalDate date = entry.getKey().atDay(1);
                    list.add(new DailySaleEarningDTO(date, BigDecimal.valueOf(profit)));

                    addDataToChart(series, month, profit, "price");
                    saleDto = (int) profit;
                    totalProfit += saleDto;
                }
            }
            if (range.equals("annual")){
                for (Map.Entry<Year, Double> entry : saleByYear.entrySet()) {
                    String year = entry.getKey().format(DateTimeFormatter.ofPattern("yyyy"));
                    int profit = entry.getValue().intValue();
                    LocalDate date = entry.getKey().atDay(1);
                    list.add(new DailySaleEarningDTO(date, BigDecimal.valueOf(profit)));

                    addDataToChart(series, year, profit, "price");
                    saleDto = (int) profit;
                    totalProfit += saleDto;
                }
            }
            barChart.setTitle("Total de venta: " + String.format("$%,d", totalProfit));
            barChart.getData().add(series);
            loadTableRank(list, "saleDate", "profit", "Fecha", "Ganancia");
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
            ObservableList<Object> list = FXCollections.observableArrayList();
            Map<YearMonth, Double> saleByMonth = calculateByMonth(dailySaleSum, dailySaleSumDTO -> (double) dailySaleSumDTO.getSum(), DailySaleSumDTO::getDate);
            Map<Year, Double> saleByYear = calculateByYear(dailySaleSum, dailySaleSumDTO -> (double) dailySaleSumDTO.getSum(), DailySaleSumDTO::getDate);

            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            if (rb30Days.isSelected() || range.equals("daily")){
                for (DailySaleSumDTO data : dailySaleSum) {
                    String date = String.valueOf(data.getDate());
                    Number sum = data.getSum();
                    list.add(data);

                    addDataToChart(series, date, sum, "price");
                    saleDto = (int) sum;
                    totalSale += saleDto;
                }
            }
            if (rb90Days.isSelected() || rb6Months.isSelected() || rb60Days.isSelected() || rbYear.isSelected() || range.equals("monthly")){
                for (Map.Entry<YearMonth, Double> entry : saleByMonth.entrySet()) {
                    String month = entry.getKey().format(DateTimeFormatter.ofPattern("MMM"));
                    int sum = entry.getValue().intValue();
                    LocalDate date = entry.getKey().atDay(1);
                    list.add(new DailySaleSumDTO(date, BigDecimal.valueOf(sum)));

                    addDataToChart(series, month, sum, "price");
                    saleDto = (int) sum;
                    totalSale += saleDto;
                }
            }
            if (range.equals("annual")){
                for (Map.Entry<Year, Double> entry : saleByYear.entrySet()) {
                    String year = entry.getKey().format(DateTimeFormatter.ofPattern("yyyy"));
                    int sum = entry.getValue().intValue();
                    LocalDate date = entry.getKey().atDay(1);
                    list.add(new DailySaleSumDTO(date, BigDecimal.valueOf(sum)));

                    addDataToChart(series, year, sum, "price");
                    saleDto = (int) sum;
                    totalSale += saleDto;
                }
            }
            barChart.setTitle("Total de venta: " + String.format("$%,d", totalSale));
            barChart.getData().add(series);
            loadTableRank(list, "date", "sum", "Fecha", "Suma de Ventas");
        }catch (Exception e){
            lblMessage.setText(e.getMessage());
        }
    }

    private void calculateProductSale(){
        try{
            LocalDate end = calculateEndDate();
            lblMessage.setText(" " + end + " - " + start);

            List<ProductSaleDTO> mostSoldProduct = reportService.getMostProductSale(start, end);
            ObservableList<Object> list = FXCollections.observableArrayList();
            barChart.getData().clear();

            Map<String, Integer> brandTotalMap = new HashMap<>();
            for (ProductSaleDTO data : mostSoldProduct) {
                String brand = data.getBrand();
                int total = data.getTotal();
                brandTotalMap.put(brand, brandTotalMap.getOrDefault(brand, 0) + total);
                list.add(data);
            }
            List<Map.Entry<String, Integer>> sortedEntries = brandTotalMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .toList();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                String brand = entry.getKey();
                int total = entry.getValue();
                addDataToChart(series, brand, total, "quantity");
            }
            barChart.setTitle("Los 10 productos más vendidos");
            barChart.getData().add(series);
            loadTableRank(list, "description", "total", "Descripción", "Cantidad");
        } catch (Exception e) {
            lblMessage.setText(e.getMessage());
        }
    }

    private void calculateCategorySale(){
        try{
            LocalDate end = calculateEndDate();
            lblMessage.setText(" " + end + " - " + start);

            List<CategoriesSaleDTO> mostSoldCategory = reportService.getMostCategorySale(start,end);
            ObservableList<Object> list = FXCollections.observableArrayList();
            barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (CategoriesSaleDTO data : mostSoldCategory) {
                String name = String.valueOf(data.getName());
                Number total = data.getTotal();
                list.add(data);
                addDataToChart(series, name, total, "quantity");
            }
            barChart.setTitle("Las 10 categorías más vendidas");
            barChart.getData().add(series);
            loadTableRank(list, "name", "total", "Categoría", "Cantidad");
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
            return end;
        }
        return null;
    }

    private <T> Map<YearMonth, Double> calculateByMonth(List<T> list, Function<T, Double> function, Function<T, LocalDate> dateFunction) {
        return list.stream()
                .collect(Collectors.groupingBy(
                        sale -> YearMonth.from(dateFunction.apply(sale)),
                        Collectors.summingDouble(function::apply)
                ));
    }

    private <T> Map<Year, Double> calculateByYear(List<T> list, Function<T, Double> function, Function<T, LocalDate> dateFunction) {
        return list.stream()
                .collect(Collectors.groupingBy(
                        sale -> Year.from(dateFunction.apply(sale)),
                        Collectors.summingDouble(function::apply)
                ));
    }

    private void addDataToChart(XYChart.Series<String, Number> series, String category, Number value, String validation){
        String name;
        XYChart.Data<String, Number> chartData = new XYChart.Data<>(category, value);
        if (validation.equals("quantity")){
            name = String.valueOf(value);
        } else {
            int item = value.intValue();
            name = String.valueOf(String.valueOf(String.format("$%,d", item)));
        }
        Label label = new Label(name);
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

    private void loadTableRank(ObservableList<Object> list, String columnAName, String columnBName, String a, String b){
        tableRank.setVisible(true);
        colA.setText(a);
        colB.setText(b);
        tableRank.setItems(list);
        colA.setCellValueFactory(new PropertyValueFactory<>(columnAName));
        colB.setCellValueFactory(new PropertyValueFactory<>(columnBName));

        configureTableView(a, b);
    }

    private void configureTableView(String columnA, String columnB) {
        if (columnA.equals("Fecha")) {
            colA.setCellFactory(col -> new TableCell<Object, Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        if (item instanceof LocalDate date) {
                            setText(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                        }else {
                            setText(item.toString());
                        }
                    }
                }
            });
        }
        colB.setCellFactory(col -> new TableCell<Object, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        if (columnB.equals("Suma de Ventas") || columnB.equals("Ganancia") ||
                columnB.equals("Promedio Venta")){
            colB.setCellFactory(col -> new TableCell<Object, Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        if (item instanceof Integer value) {
                            setText(String.format("$%,d", value));
                        }else {
                            setText(item.toString());
                        }
                    }
                }
            });
        }
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