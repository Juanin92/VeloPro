package cl.jic.VeloPro.Controller.Report;

import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Service.Sale.Interfaces.ISaleService;
import cl.jic.VeloPro.Utility.ButtonManager;
import cl.jic.VeloPro.Utility.ExcelGenerator;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.Utility.PDFGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class SaleRegisterController implements Initializable {

    @FXML private TableColumn<Sale, Void> colAction;
    @FXML private TableColumn<Sale, String> colComment;
    @FXML private TableColumn<Sale, LocalDate> colDate;
    @FXML private TableColumn<Sale, String> colDoc;
    @FXML private TableColumn<Sale, String> colMethod;
    @FXML private TableColumn<Sale, Integer> colTax;
    @FXML private TableColumn<Sale, Integer> colTotal;
    @FXML private TableView<Sale> saleTable;
    @FXML private Button btnExcel, btnOk, btnClean;
    @FXML private DatePicker dateTo, dateFrom;

    @Autowired private ISaleService saleService;
    @Autowired private PDFGenerator pdfGenerator;
    @Autowired private ExcelGenerator excelGenerator;
    @Autowired private ButtonManager buttonManager;
    @Autowired private NotificationManager notificationManager;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));
    private  ObservableList<Sale> saleList;
    private ObservableList<Sale> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDataSaleList();
        cleanFilteredList();
        btnClean.setOnAction(event -> cleanFilteredList());
        btnExcel.setOnAction(event -> {
            try {
                ObservableList<Sale> listToExport = (filteredList != null && !filteredList.isEmpty()) ? filteredList : saleList;
                excelGenerator.createSaleFile(listToExport, "Ventas");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void loadDataSaleList(){
        saleList = FXCollections.observableArrayList(saleService.getAll());
        saleTable.setItems(saleList);

        colDoc.setCellValueFactory(new PropertyValueFactory<>("document"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colTax.setCellValueFactory(new PropertyValueFactory<>("tax"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalSale"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        configurationTableView();
    }

    private void cleanFilteredList() {
        filteredList = null;
        saleTable.setItems(saleList);
        dateFrom.setValue(null);
        dateTo.setValue(null);
    }

    @FXML
    private void handleFilterByDate() {
        LocalDate fromDate = dateFrom.getValue();
        LocalDate toDate = dateTo.getValue();

        if (fromDate != null && toDate != null && !fromDate.isAfter(toDate)) {
            filteredList = saleList.filtered(sale -> {
                LocalDate saleDate = sale.getDate();
                return (saleDate != null && (saleDate.isEqual(fromDate) || saleDate.isEqual(toDate) ||
                        (saleDate.isAfter(fromDate) && saleDate.isBefore(toDate))));
            });
            saleTable.setItems(filteredList);
        } else {
            notificationManager.warningNotification("Fechas no válidas", "Por favor, seleccione un rango de fechas válido.", Pos.CENTER);
        }
    }

    private void configurationTableView() {
        Callback<TableColumn<Sale, Void>, TableCell<Sale, Void>> cellFactory = new Callback<TableColumn<Sale, Void>, TableCell<Sale, Void>>() {

            @Override
            public TableCell<Sale, Void> call(final TableColumn<Sale, Void> param) {
                return new TableCell<Sale, Void>() {
                    private final Button btnPrint = buttonManager.createButton("iconPrint.png", "transparent", 30, 30);
                    private final Button btnVoid = buttonManager.createButton("iconCancel.png", "transparent", 30, 30);
                    {
                        btnPrint.setOnAction((event) -> {
                            Sale sale = getTableView().getItems().get(getIndex());
                            String filePath = "C:\\Users\\juano\\Desktop\\Boletas PDF\\"+ sale.getDocument() + ".pdf";
                            pdfGenerator.openPDF(filePath);
                        });
                        btnVoid.setOnAction(event -> {
                            Sale sale = getTableView().getItems().get(getIndex());
                            String filePath = "C:\\Users\\juano\\Desktop\\Boletas PDF\\"+ sale.getDocument() + ".pdf";
                            pdfGenerator.addWatermarkToPDF(filePath);
                            saleService.saleRegisterVoid(sale);
                            saleTable.refresh();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Sale sale = getTableView().getItems().get(getIndex());
                            btnVoid.setDisable(!sale.isStatus());
                            HBox buttons = new HBox(btnPrint, btnVoid);
                            buttons.setAlignment(Pos.CENTER);
                            buttons.setSpacing(5);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);

        colTax.setCellFactory(column -> new TableCell<Sale, Integer>() {
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
        colTotal.setCellFactory(column -> new TableCell<Sale, Integer>() {
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
}