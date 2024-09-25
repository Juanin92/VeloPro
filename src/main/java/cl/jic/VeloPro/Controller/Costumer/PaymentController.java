package cl.jic.VeloPro.Controller.Costumer;

import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.Costumer.PaymentCostumer;
import cl.jic.VeloPro.Model.Entity.Costumer.TicketHistory;
import cl.jic.VeloPro.Service.Costumer.Interface.ICostumerService;
import cl.jic.VeloPro.Service.Costumer.Interface.IPaymentCostumerService;
import cl.jic.VeloPro.Service.Costumer.Interface.ITicketHistoryService;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import lombok.Setter;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PaymentController implements Initializable {

    @FXML private Label lblDebtCostumer, lblPayment, lblTotalDebt;
    @FXML private CustomTextField txtPayment;
    @FXML private TableColumn<PaymentCostumer, Integer> colAmount;
    @FXML private TableColumn<PaymentCostumer, String> colComment;
    @FXML private TableColumn<PaymentCostumer, LocalDate> colDate;
    @FXML private TableColumn<PaymentCostumer, TicketHistory> colDocument;
    @FXML private TableView<PaymentCostumer> listPaymentCostumer;
    @FXML private ComboBox<TicketHistory> cbTicket;
    @FXML private ComboBox<String> cbComment;
    @FXML private Button btnSavePayment;

    @Autowired private ICostumerService costumerService;
    @Autowired private IPaymentCostumerService paymentCostumerService;
    @Autowired private ITicketHistoryService ticketHistoryService;
    @Autowired private ICostumerList ICostumerList;
    @Autowired private GraphicsValidator graphicsValidator;
    @Setter private Costumer currentCostumer;

    ObservableList<PaymentCostumer> paymentList;
    List<TicketHistory> selectedTickets;
    private int totalPayment;
    private int debt;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(this::loadCombo);
        Platform.runLater(this::loadDataPaymentList);
        Platform.runLater(this::calculateTotalPayment);
    }

    @FXML
    public void paymentDebtCostumer(){
        try {
            costumerService.paymentDebt(currentCostumer, txtPayment.getText());
            PaymentCostumer paymentCostumer = new PaymentCostumer();
            paymentCostumer.setCostumer(currentCostumer);
            paymentCostumer.setAmount(Integer.parseInt(txtPayment.getText()));
            paymentCostumer.setDocument(cbTicket.getValue());
            paymentCostumer.setComment(cbComment.getValue());
            paymentCostumerService.addPayments(paymentCostumer);

            int selectedIndex = cbTicket.getSelectionModel().getSelectedIndex();
            if (selectedIndex + 1 < cbTicket.getItems().size()) {
                validateDebt(cbTicket.getItems().get(selectedIndex + 1), Integer.parseInt(txtPayment.getText()));
            } else {
                validateDebt(cbTicket.getValue(), Integer.parseInt(txtPayment.getText()));
            }
            loadDataPaymentList();
            calculateTotalPayment();
            ICostumerList.loadDataCostumerList();
            ICostumerList.updateTotalDebt();
            loadCombo();
        } catch (NumberFormatException e){
            handleValidationException("Ingrese solo números.");
        } catch (IllegalArgumentException e) {
            handleValidationException("Seleccione una forma de pago");
            handleValidationException(e.getMessage());
        }
    }

    private void validateDebt(TicketHistory ticketHistory, int amount) {
        for (TicketHistory ticket : selectedTickets) {
            if (selectedTickets.size() > 1){
                if (ticket.getTotal() <=  amount){
                    ticketHistoryService.updateStatus(ticket);
                    addAdjustPayment(amount - ticket.getTotal(), ticketHistory);
                    break;
                }
            } else {
                if (ticket.getTotal() <= (totalPayment + amount)){
                    ticketHistoryService.updateStatus(ticket);
                    addAdjustPayment(amount - ticket.getTotal(), ticketHistory);
                    break;
                }
            }
        }
    }

    private void addAdjustPayment(int amount, TicketHistory ticketHistory) {
        if (amount > 0) {
            PaymentCostumer paymentCostumer = new PaymentCostumer();
            paymentCostumer.setCostumer(currentCostumer);
            paymentCostumer.setDocument(ticketHistory);
            paymentCostumer.setAmount(amount);
            paymentCostumer.setComment("Ajuste");
            paymentCostumerService.addPayments(paymentCostumer);
        }
    }

    private void calculateTotalPayment() {
        totalPayment = 0;
        for (PaymentCostumer payment : paymentList) {
            totalPayment += payment.getAmount();
        }
        lblPayment.setText(String.format("$%,d", totalPayment));
        lblDebtCostumer.setText(String.format("$%,d", debt - totalPayment));
    }

    public void loadDataPaymentList(){
        List<PaymentCostumer> filteredPayments = paymentCostumerService.getCostumerSelected(currentCostumer.getId())
                .stream()
                .filter(payment -> selectedTickets.stream()
                        .anyMatch(ticket -> Objects.equals(ticket.getId(), payment.getDocument().getId())))
                .sorted(Comparator.comparing(PaymentCostumer::getDate))
                .collect(Collectors.toList());

        paymentList = FXCollections.observableArrayList(filteredPayments);
        listPaymentCostumer.setItems(paymentList);

        colDate.setCellValueFactory(new PropertyValueFactory<PaymentCostumer,LocalDate>("date"));
        colAmount.setCellValueFactory(new PropertyValueFactory<PaymentCostumer,Integer>("amount"));
        colDocument.setCellValueFactory(new PropertyValueFactory<PaymentCostumer, TicketHistory>("document"));
        colComment.setCellValueFactory(new PropertyValueFactory<PaymentCostumer,String>("comment"));

        configurationTableView();
        calculateTotalPayment();
    }

    private void configurationTableView() {
        colAmount.setCellFactory(column -> new TableCell<PaymentCostumer,Integer>(){
            @Override
            protected void updateItem(Integer item,boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));
                    setText(currencyFormat.format(item));
                    autosize();
                    if (item > 0){
                        setTextFill(Color.RED);
                    }
                }
            }
        });
    }

    private void loadCombo(){
        debt = 0;
        List<TicketHistory> tickets = ticketHistoryService.getByCostumerId(currentCostumer.getId())
                .stream()
                .sorted(Comparator.comparing(TicketHistory::getTotal))
                .toList();

        for (TicketHistory ticket : tickets){
            if (!ticket.isStatus()){
                cbTicket.getItems().add((ticket));
                debt += ticket.getTotal();
                lblTotalDebt.setText(String.format("$%,d", debt));
            }
        }
        cbTicket.getSelectionModel().select(0);
        selectedTickets = cbTicket.getItems();

        cbComment.getItems().addAll("Efectivo", "Tarjeta", "Otro");
        txtPayment.clear();
    }

    private void handleValidationException(String errorMessage){
        switch (errorMessage){
            case "Seleccione una forma de pago":
                graphicsValidator.settingAndValidationComboBox(cbComment, true, errorMessage);
                break;
            case "Ingrese solo números.":
            case "El monto no puede ser menor a 0.":
            case "El monto supera el valor de la deuda.":
                graphicsValidator.settingAndValidationTextField(txtPayment,true, errorMessage);
                break;
        }
    }
}