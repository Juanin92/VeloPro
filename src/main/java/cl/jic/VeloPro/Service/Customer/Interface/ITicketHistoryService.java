package cl.jic.VeloPro.Service.Customer.Interface;

import cl.jic.VeloPro.Model.Entity.Customer.Customer;
import cl.jic.VeloPro.Model.Entity.Customer.TicketHistory;

import java.time.LocalDate;
import java.util.List;

public interface ITicketHistoryService{

    void AddTicketToCustomer(Customer customer, Long number, int total, LocalDate date);
    List<TicketHistory> getAll();
    List<TicketHistory> getByCustomerId(Long id);
    void valideTicketByCustomer(Customer customer);
    void updateStatus(TicketHistory  ticket);
}
