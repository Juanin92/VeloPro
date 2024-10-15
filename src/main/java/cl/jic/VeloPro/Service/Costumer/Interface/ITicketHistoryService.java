package cl.jic.VeloPro.Service.Costumer.Interface;

import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.Costumer.TicketHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ITicketHistoryService{

    void AddTicketToCostumer(Costumer costumer, Long number, int total, LocalDate date);
    List<TicketHistory> getAll();
    List<TicketHistory> getByCostumerId(Long id);
    boolean validateDate(TicketHistory ticket);
    void updateStatus(TicketHistory  ticket);
}
