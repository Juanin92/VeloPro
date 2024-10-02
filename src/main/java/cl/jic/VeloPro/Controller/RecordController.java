package cl.jic.VeloPro.Controller;

import cl.jic.VeloPro.Model.Entity.Record;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.Rol;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
public class RecordController implements Initializable {

    @FXML private TableColumn<Record, String> colAction;
    @FXML private TableColumn<Record, String> colComment;
    @FXML private TableColumn<Record, LocalDateTime> colDateAction;
    @FXML private TableColumn<Record, LocalDateTime> colEntry;
    @FXML private TableColumn<Record, LocalDateTime> colExit;
    @FXML private TableColumn<Record, User> colUser;
    @FXML private TableView<Record> tableRecord;
    @FXML private AnchorPane paneConfirmUser;
    @FXML private CustomTextField txtSearch;
    @FXML private CustomPasswordField txtPassword;

    @Autowired private IRecordService recordService;
    @Autowired private Session session;
    private ObservableList<Record> list;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        confirmUser();
        loadDataCashRegisterList();
        setupSearchFilter();
    }

    private void confirmUser(){
        if(session.getCurrentUser().getRole().equals(Rol.MASTER)){
            txtPassword.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER){
                    if (session.getCurrentUser().getPassword().equals(txtPassword.getText())){
                        paneConfirmUser.setVisible(false);
                        tableRecord.setVisible(true);
                        txtSearch.setVisible(true);
                    } else {
                        Stage stage = (Stage) txtPassword.getScene().getWindow();
                        stage.close();
                    }
                }
            });
        }
    }

    public void loadDataCashRegisterList() {
        list = FXCollections.observableArrayList(recordService.getAllRecord());
        tableRecord.setItems(list);

        colUser.setCellValueFactory(new PropertyValueFactory<>("user"));
        colEntry.setCellValueFactory(new PropertyValueFactory<>("entryDate"));
        colExit.setCellValueFactory(new PropertyValueFactory<>("endaDate"));
        colDateAction.setCellValueFactory(new PropertyValueFactory<>("actionDate"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        configurationTableView();
    }

    private void configurationTableView(){
        colUser.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " " + item.getSurname());
                    autosize();
                }
            }
        });
        colEntry.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                    autosize();
                }
            }
        });
        colExit.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                    autosize();
                }
            }
        });
        colDateAction.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                    autosize();
                }
            }
        });
    }

    @FXML
    private void setupSearchFilter() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty() || newValue.isBlank()) {
                tableRecord.setItems(list);
                tableRecord.refresh();
            } else {
                String word = newValue.toLowerCase();
                ObservableList<Record> filteredList = FXCollections.observableArrayList();
                for (Record record : list) {
                    if (record.getUser().getName().toLowerCase().contains(word) ||
                            record.getUser().getSurname().toLowerCase().contains(word) ||
                            record.getAction().toLowerCase().equals(word)) {
                        filteredList.add(record);
                    }
                }
                tableRecord.setItems(filteredList);
            }
        });
    }
}
