package cl.jic.VeloPro.Service.Costumer.Interface;

import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.Costumer.PaymentCostumer;
import cl.jic.VeloPro.Model.Entity.Costumer.TicketHistory;

import java.util.List;

public interface IPaymentCostumerService {

    void addPayments(PaymentCostumer costumer);
    void addAdjustPayments(int amount, TicketHistory ticket, Costumer costumer);
    List<PaymentCostumer> getAll();
    List<PaymentCostumer> getCostumerSelected(Long id);
    int calculateDebtTicket(TicketHistory ticket);
}
