package cl.jic.VeloPro.Service.Costumer;

import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.Costumer.TicketHistory;
import cl.jic.VeloPro.Repository.Costumer.TicketHistoryRepo;
import cl.jic.VeloPro.Service.Costumer.Interface.ITicketHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TicketHistoryService implements ITicketHistoryService {

    @Autowired private TicketHistoryRepo ticketHistoryRepo;

    @Override
    public void AddTicketToCostumer(Costumer costumer, Long number, int total, LocalDate date) {
        TicketHistory ticket = new TicketHistory();
        String name = "BO00" + number;
        ticket.setTotal(total);
        ticket.setDocument(name);
        ticket.setStatus(false);
        ticket.setDate(date);
        ticket.setCostumer(costumer);
        ticketHistoryRepo.save(ticket);
    }

    @Override
    public List<TicketHistory> getAll() {
        return ticketHistoryRepo.findAll();
    }

    @Override
    public List<TicketHistory> getByCostumerId(Long id) {
        return ticketHistoryRepo.findByCostumerId(id);
    }

    @Override
    public void deleteTickets(TicketHistory ticket) {
        ticketHistoryRepo.delete(ticket);
    }

    @Override
    public boolean validateDate(TicketHistory ticket) {
        LocalDate now = LocalDate.now();
        LocalDate ticketDate = ticket.getDate();
        LocalDate notificationDate = ticket.getNotificationsDate();

        if (ChronoUnit.DAYS.between(ticketDate, now) > 30 &&
                (notificationDate == null || ChronoUnit.DAYS.between(notificationDate, now) > 15)) {
            ticket.setNotificationsDate(now);
            ticketHistoryRepo.save(ticket);
            return true;
        }
        return false;
    }

    @Override
    public void updateStatus(TicketHistory ticket) {
        ticket.setStatus(true);
        ticketHistoryRepo.save(ticket);
    }
}
