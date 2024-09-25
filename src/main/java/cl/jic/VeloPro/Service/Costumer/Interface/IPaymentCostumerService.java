package cl.jic.VeloPro.Service.Costumer.Interface;

import cl.jic.VeloPro.Model.Entity.Costumer.PaymentCostumer;
import cl.jic.VeloPro.Model.Entity.Costumer.TicketHistory;

import java.util.List;

public interface IPaymentCostumerService {

    void addPayments(PaymentCostumer costumer);
    List<PaymentCostumer> getAll();
    List<PaymentCostumer> getCostumerSelected(Long id);
    int calculateDebtTicket(TicketHistory ticket);
    void deletePayments(PaymentCostumer costumer);
}
