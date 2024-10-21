package cl.jic.VeloPro.Controller.Costumer;

import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.Costumer.PaymentCostumer;
import cl.jic.VeloPro.Model.Entity.Costumer.TicketHistory;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Service.Costumer.Interface.ICostumerService;
import cl.jic.VeloPro.Service.Costumer.Interface.IPaymentCostumerService;
import cl.jic.VeloPro.Service.Costumer.Interface.ITicketHistoryService;
import cl.jic.VeloPro.Service.Record.IRecordService;
import cl.jic.VeloPro.Session.Session;
import cl.jic.VeloPro.Utility.NotificationManager;
import cl.jic.VeloPro.Validation.GraphicsValidator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import lombok.Setter;
import org.controlsfx.control.CheckListView;
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
    @FXML private Button btnSavePayment;
    @FXML private CustomTextField txtPayment;
    @FXML private TableColumn<PaymentCostumer, Integer> colAmount;
    @FXML private TableColumn<PaymentCostumer, String> colComment;
    @FXML private TableColumn<PaymentCostumer, LocalDate> colDate;
    @FXML private TableColumn<PaymentCostumer, TicketHistory> colDocument;
    @FXML private TableView<PaymentCostumer> listPaymentCostumer;
    @FXML private ComboBox<String> cbComment;
    @FXML private CheckListView<TicketHistory> cxListViewTicket;

    @Autowired private ICostumerService costumerService;
    @Autowired private IPaymentCostumerService paymentCostumerService;
    @Autowired private ITicketHistoryService ticketHistoryService;
    @Autowired private ICostumerList ICostumerList;
    @Autowired private IRecordService recordService;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private NotificationManager notificationManager;
    @Autowired private Session session;
    @Setter private Costumer currentCostumer;

    ObservableList<PaymentCostumer> paymentList;
    ObservableList<TicketHistory> selectedTicket;
    List<TicketHistory> allTickets;
    private User currentUser;
    private int totalPayment;
    private int debt;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = session.getCurrentUser();
        Platform.runLater(this::loadData);
        Platform.runLater(this::loadDataPaymentList);
        Platform.runLater(this::calculateTotalPayment);
    }

    @FXML
    public void validateDebt() {
        try {
            int totalSelectedTickets = 0;
            int amount = Integer.parseInt(txtPayment.getText());
            if (selectedTicket.isEmpty()) {
                notificationManager.warningNotification("Advertencia", "Seleccione una o varias boletas", Pos.CENTER);
            } else {
                for (TicketHistory ticket : selectedTicket) {
                    totalSelectedTickets += ticket.getTotal();
                }
                if (selectedTicket.size() > 1) {
                    if (amount == (totalSelectedTickets)) {
                        for (TicketHistory ticket : selectedTicket) {
                            paymentDebtCostumer(ticket, cbComment.getValue());
                            ticketHistoryService.updateStatus(ticket);
                        }
                    } else {
                        handleValidationException("El monto no es correcto para el pago de la deuda.");
                    }
                } else {
                    if (amount == (totalSelectedTickets - totalPayment)) {
                        paymentDebtCostumer(selectedTicket.getFirst(), cbComment.getValue());
                        ticketHistoryService.updateStatus(selectedTicket.getFirst());
                    } else if (amount < (totalSelectedTickets - totalPayment)) {
                        paymentCostumerService.addAdjustPayments(amount, selectedTicket.getFirst(), currentCostumer);
                        costumerService.paymentDebt(currentCostumer, txtPayment.getText());
                    }else {
                       handleValidationException("El monto supera el valor de la deuda.");
                    }
                }
                loadData();
                loadDataPaymentList();
                cleanInfo();
            }
        }catch (NumberFormatException ex){
            handleValidationException("Ingrese solo números.");
        }catch (IllegalArgumentException e){
            handleValidationException("Seleccione una forma de pago");
            handleValidationException(e.getMessage());
        }
    }

    private void paymentDebtCostumer(TicketHistory ticket, String comment){
        PaymentCostumer paymentCostumer = new PaymentCostumer();
        paymentCostumer.setCostumer(currentCostumer);
        paymentCostumer.setDocument(ticket);
        paymentCostumer.setComment(comment);
        paymentCostumerService.addPayments(paymentCostumer);
        paymentCostumer.setAmount(ticket.getTotal());
        costumerService.paymentDebt(currentCostumer, String.valueOf(ticket.getTotal()));

        ICostumerList.loadDataCostumerList();
        ICostumerList.updateTotalDebtLabel();
        notificationManager.successNotification("Éxito!", "Se ha agregado el abono al cliente - N° Doc: " + ticket.getDocument(), Pos.CENTER);
        recordService.registerAction(currentUser, "PAYMENT", "Pago Cliente " + currentCostumer.getName()
                + " " + currentCostumer.getSurname() + ", Cantidad: " + Integer.parseInt(txtPayment.getText()));
    }

    private void calculateTotalPayment() {
        totalPayment = 0;
        for (PaymentCostumer payment : paymentList) {
            totalPayment += payment.getAmount();
        }
        if(selectedTicket.isEmpty()){
            lblDebtCostumer.setText(String.format("$%,d", 0));
        }else {
            lblDebtCostumer.setText(String.format("$%,d", debt - totalPayment));
        }
        lblPayment.setText(String.format("$%,d", totalPayment));
    }

    public void loadDataPaymentList(){
        List<PaymentCostumer> filteredPayments = paymentCostumerService.getCostumerSelected(currentCostumer.getId())
                .stream()
                .filter(payment -> allTickets.stream()
                        .anyMatch(ticket -> Objects.equals(ticket.getId(), payment.getDocument().getId())))
                .sorted(Comparator.comparing(PaymentCostumer::getDate))
                .collect(Collectors.toList());

        paymentList = FXCollections.observableArrayList(filteredPayments);
        listPaymentCostumer.setItems(paymentList);

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDocument.setCellValueFactory(new PropertyValueFactory<>("document"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        configurationTableView();
        calculateTotalPayment();
    }

    private void configurationTableView() {
        colAmount.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));
                    setText(currencyFormat.format(item));
                    autosize();
                    if (item > 0) {
                        setTextFill(Color.RED);
                    }
                }
            }
        });
    }

    private void loadData(){
        int selectedDebt = 0;
        debt = 0;
        cxListViewTicket.getItems().clear();
        List<TicketHistory> tickets = ticketHistoryService.getByCostumerId(currentCostumer.getId())
                .stream()
                .sorted(Comparator.comparing(TicketHistory::getTotal))
                .toList();
        for (TicketHistory ticket : tickets){
            if (!ticket.isStatus()){
                cxListViewTicket.getItems().add(ticket);
                selectedDebt += ticket.getTotal();
                lblTotalDebt.setText(String.format("$%,d", selectedDebt));
            }
        }
        cxListViewTicket.getCheckModel().getCheckedItems().addListener((ListChangeListener<TicketHistory>) change -> {
            while (change.next()) {
                change.getAddedSubList().forEach(ticket -> debt += ticket.getTotal());
                change.getRemoved().forEach(ticket -> debt -= ticket.getTotal());
            }
            lblDebtCostumer.setText(String.format("$%,d", (debt - totalPayment)));
        });
        allTickets = cxListViewTicket.getItems();
        cbComment.getItems().addAll("Efectivo", "Tarjeta", "Otro");
        selectedTicket = cxListViewTicket.getCheckModel().getCheckedItems();
    }

    private void cleanInfo(){
        txtPayment.clear();
        cxListViewTicket.refresh();
        cxListViewTicket.getCheckModel().clearChecks();
        lblDebtCostumer.setText("0");
    }

    private void handleValidationException(String errorMessage){
        switch (errorMessage){
            case "Seleccione una forma de pago":
                graphicsValidator.settingAndValidationComboBox(cbComment, true, errorMessage);
                break;
            case "Ingrese solo números.":
            case "El monto no puede ser menor a 0.":
            case "El monto supera el valor de la deuda.":
            case "El monto no es correcto para el pago de la deuda.":
                graphicsValidator.settingAndValidationTextField(txtPayment,true, errorMessage);
                break;
        }
    }
}