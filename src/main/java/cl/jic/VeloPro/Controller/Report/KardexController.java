package cl.jic.VeloPro.Controller.Report;

import cl.jic.VeloPro.Model.Entity.Kardex;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Model.Enum.MovementsType;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Service.Report.Interfaces.IKardexService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.ExcelGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
public class KardexController implements Initializable {

    @FXML private Button btnPrint;
    @FXML private TableColumn<Kardex, String> colComment;
    @FXML private TableColumn<Kardex, LocalDate> colDate;
    @FXML private TableColumn<Kardex, String> colDes;
    @FXML private TableColumn<Kardex, Long> colId;
    @FXML private TableColumn<Kardex, MovementsType> colMov;
    @FXML private TableColumn<Kardex, Integer> colPrice;
    @FXML private TableColumn<Kardex, Integer> colQuantity;
    @FXML private TableColumn<Kardex, Integer> colStock;
    @FXML private TableColumn<Kardex, String> colUser;
    @FXML private TableView<Kardex> tableKardek;
    @FXML private CustomTextField txtSearch;

    @Autowired private IKardexService kardexService;
    @Autowired private IRecordService recordService;
    @Autowired private ExcelGenerator excelGenerator;
    @Autowired private Session session;

    private ObservableList<Kardex> kardexList;
    private ObservableList<Kardex> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDataKardexList();
        setupSearchFilterRegister();
        btnPrint.setOnAction(event -> {
            try {
                ObservableList<Kardex> listToExport = (filteredList != null && !filteredList.isEmpty()) ? filteredList : kardexList;
                excelGenerator.createKardexFile(listToExport, "Kardex");
                recordService.registerAction(session.getCurrentUser(), "CREATE", "Crea Archivo kardex");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        recordService.registerAction(session.getCurrentUser(), "VIEW", "kardex");
    }

    public void loadDataKardexList(){
        kardexList = FXCollections.observableArrayList(kardexService.getAll());
        tableKardek.setItems(kardexList);

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDes.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colMov.setCellValueFactory(new PropertyValueFactory<>("movementsType"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("user"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        configurationTableView();
    }

    private void configurationTableView() {
        colMov.setCellFactory(column -> new TableCell<Kardex,MovementsType>(){
            @Override
            protected void updateItem(MovementsType item,boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null){
                    setText("");
                    setStyle("");
                }else {
                    setText(item.toString());
                    if (item.equals(MovementsType.ENTRADA)){
                        setStyle("-fx-background-color: green;");
                    } else if (item.equals(MovementsType.SALIDA)) {
                        setStyle("-fx-background-color: red;");
                    } else{
                        setStyle("-fx-background-color: yellow;");
                    }
                    setTextFill(Color.BLACK);
                    autosize();
                }
            }
        });
        colPrice.setCellFactory(column -> new TableCell<Kardex, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(String.format("$%,d", item)));
                    autosize();
                }
            }
        });
        colDes.setCellValueFactory(cellData -> {
            Kardex kardex = cellData.getValue();
            String description = (kardex.getProduct() != null) ? kardex.getProduct().getDescription() : "";
            return new SimpleStringProperty(description);
        });
        colUser.setCellValueFactory(cellData -> {
            Kardex kardex = cellData.getValue();
            String user = (kardex.getUser() != null) ? kardex.getUser().getName() + " " + kardex.getUser().getSurname() : "";
            return new SimpleStringProperty(user);
        });
    }

    @FXML
    private void setupSearchFilterRegister() {
        txtSearch .textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                tableKardek.setItems(kardexList);
                tableKardek.refresh();
                filteredList.clear();
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                filteredList = FXCollections.observableArrayList();
                for (Kardex kardex : kardexList) {
                    String description = kardex.getProduct().getDescription().toLowerCase();
                    if (kardex.getProduct().getDescription().toLowerCase().contains(lowerCaseFilter) ||
                            description.contains(lowerCaseFilter)) {
                        filteredList.add(kardex);
                    }
                }
                tableKardek.setItems(filteredList);
            }
        });
    }
}