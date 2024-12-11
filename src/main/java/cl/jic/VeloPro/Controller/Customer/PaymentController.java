package cl.jic.VeloPro.Controller.Customer;

import cl.jic.VeloPro.Model.Entity.Customer.Customer;
import cl.jic.VeloPro.Model.Entity.Customer.PaymentCustomer;
import cl.jic.VeloPro.Model.Entity.Customer.TicketHistory;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Service.Customer.Interface.ICustomerService;
import cl.jic.VeloPro.Service.Customer.Interface.IPaymentCustomerService;
import cl.jic.VeloPro.Service.Customer.Interface.ITicketHistoryService;
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

    @FXML private Label lblDebtCustomer, lblPayment, lblTotalDebt;
    @FXML private Button btnSavePayment;
    @FXML private CustomTextField txtPayment;
    @FXML private TableColumn<PaymentCustomer, Integer> colAmount;
    @FXML private TableColumn<PaymentCustomer, String> colComment;
    @FXML private TableColumn<PaymentCustomer, LocalDate> colDate;
    @FXML private TableColumn<PaymentCustomer, TicketHistory> colDocument;
    @FXML private TableView<PaymentCustomer> listPaymentCustomer;
    @FXML private ComboBox<String> cbComment;
    @FXML private CheckListView<TicketHistory> cxListViewTicket;

    @Autowired private ICustomerService customerService;
    @Autowired private IPaymentCustomerService paymentCustomerService;
    @Autowired private ITicketHistoryService ticketHistoryService;
    @Autowired private ICustomerList ICustomerList;
    @Autowired private IRecordService recordService;
    @Autowired private GraphicsValidator graphicsValidator;
    @Autowired private NotificationManager notificationManager;
    @Autowired private Session session;
    @Setter private Customer currentCustomer;

    ObservableList<PaymentCustomer> paymentList;
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
                            paymentDebtCustomer(ticket, cbComment.getValue());
                            ticketHistoryService.updateStatus(ticket);
                        }
                    } else {
                        handleValidationException("El monto no es correcto para el pago de la deuda.");
                    }
                } else {
                    if (amount == (totalSelectedTickets - totalPayment)) {
                        paymentDebtCustomer(selectedTicket.getFirst(), cbComment.getValue());
                        ticketHistoryService.updateStatus(selectedTicket.getFirst());
                    } else if (amount < (totalSelectedTickets - totalPayment)) {
                        paymentCustomerService.addAdjustPayments(amount, selectedTicket.getFirst(), currentCustomer);
                        customerService.paymentDebt(currentCustomer, txtPayment.getText());
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

    private void paymentDebtCustomer(TicketHistory ticket, String comment){
        PaymentCustomer paymentCustomer = new PaymentCustomer();
        paymentCustomer.setCustomer(currentCustomer);
        paymentCustomer.setDocument(ticket);
        paymentCustomer.setComment(comment);
        paymentCustomerService.addPayments(paymentCustomer);
        paymentCustomer.setAmount(ticket.getTotal());
        customerService.paymentDebt(currentCustomer, String.valueOf(ticket.getTotal()));

        ICustomerList.loadDataCustomerList();
        ICustomerList.updateTotalDebtLabel();
        notificationManager.successNotification("Éxito!", "Se ha agregado el abono al cliente - N° Doc: " + ticket.getDocument(), Pos.CENTER);
        recordService.registerAction(currentUser, "PAYMENT", "Pago Cliente " + currentCustomer.getName()
                + " " + currentCustomer.getSurname() + ", Cantidad: " + Integer.parseInt(txtPayment.getText()));
    }

    private void calculateTotalPayment() {
        totalPayment = 0;
        for (PaymentCustomer payment : paymentList) {
            totalPayment += payment.getAmount();
        }
        if(selectedTicket.isEmpty()){
            lblDebtCustomer.setText(String.format("$%,d", 0));
        }else {
            lblDebtCustomer.setText(String.format("$%,d", debt - totalPayment));
        }
        lblPayment.setText(String.format("$%,d", totalPayment));
    }

    public void loadDataPaymentList(){
        List<PaymentCustomer> filteredPayments = paymentCustomerService.getCustomerSelected(currentCustomer.getId())
                .stream()
                .filter(payment -> allTickets.stream()
                        .anyMatch(ticket -> Objects.equals(ticket.getId(), payment.getDocument().getId())))
                .sorted(Comparator.comparing(PaymentCustomer::getDate))
                .collect(Collectors.toList());

        paymentList = FXCollections.observableArrayList(filteredPayments);
        listPaymentCustomer.setItems(paymentList);

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
        List<TicketHistory> tickets = ticketHistoryService.getByCustomerId(currentCustomer.getId())
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
            lblDebtCustomer.setText(String.format("$%,d", (debt - totalPayment)));
        });
        allTickets = cxListViewTicket.getItems();
        cbComment.getItems().addAll("Efectivo", "Tarjeta", "Otro");
        selectedTicket = cxListViewTicket.getCheckModel().getCheckedItems();
    }

    private void cleanInfo(){
        txtPayment.clear();
        cxListViewTicket.refresh();
        cxListViewTicket.getCheckModel().clearChecks();
        lblDebtCustomer.setText("0");
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