package cl.jic.VeloPro.Service.Customer.Interface;

import cl.jic.VeloPro.Model.Entity.Customer.Customer;
import cl.jic.VeloPro.Model.Entity.Customer.PaymentCustomer;
import cl.jic.VeloPro.Model.Entity.Customer.TicketHistory;

import java.util.List;

public interface IPaymentCustomerService {

    void addPayments(PaymentCustomer customer);
    void addAdjustPayments(int amount, TicketHistory ticket, Customer customer);
    List<PaymentCustomer> getAll();
    List<PaymentCustomer> getCustomerSelected(Long id);
    int calculateDebtTicket(TicketHistory ticket);
}
