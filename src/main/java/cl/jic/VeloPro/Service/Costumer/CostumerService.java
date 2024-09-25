package cl.jic.VeloPro.Service.Costumer;

import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.Costumer.TicketHistory;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Model.Enum.PaymentStatus;
import cl.jic.VeloPro.Repository.Costumer.CostumerRepo;
import cl.jic.VeloPro.Repository.Costumer.TicketHistoryRepo;
import cl.jic.VeloPro.Repository.Sale.SaleRepo;
import cl.jic.VeloPro.Service.Costumer.Interface.ICostumerService;
import cl.jic.VeloPro.Validation.CostumerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CostumerService implements ICostumerService {

    @Autowired private CostumerRepo costumerRepo;
    @Autowired private TicketHistoryRepo ticketHistoryRepo;
    @Autowired private CostumerValidator validator;

    @Override
    public void addNewCostumer(Costumer costumer) {
        if (costumer.getEmail() == null || costumer.getEmail().isEmpty()){
            costumer.setEmail("x@x.xxx");
        }
        validator.validate(costumer);
        costumer.setAccount(true);
        costumer.setStatus(PaymentStatus.NULO);
        costumer.setTotalDebt(0);
        costumer.setDebt(0);
        costumer.setName(costumer.getName().substring(0,1).toUpperCase() + costumer.getName().substring(1));
        costumer.setSurname(costumer.getSurname().substring(0,1).toUpperCase() + costumer.getSurname().substring(1));
        costumerRepo.save(costumer);
    }

    @Override
    public void updateCostumer(Costumer costumer) {
        if (costumer.getEmail() == null || costumer.getEmail().isEmpty()){
            costumer.setEmail("x@x.xxx");
        }
        validator.validate(costumer);
        costumer.setName(costumer.getName().substring(0,1).toUpperCase() + costumer.getName().substring(1));
        costumer.setSurname(costumer.getSurname().substring(0,1).toUpperCase() + costumer.getSurname().substring(1));
        costumerRepo.save(costumer);
    }

    @Override
    public List<Costumer> getAll() {
        return costumerRepo.findAll();
    }

    @Override
    public void delete(Costumer costumer) {
        costumer.setAccount(false);
        costumerRepo.save(costumer);
    }

    @Override
    public void paymentDebt(Costumer costumer,String amount) {
        int number = Integer.parseInt(amount);
        validator.validateValuePayment(number, costumer);
        costumer.setDebt(costumer.getDebt() - number);
        costumerRepo.save(costumer);
        statusAssign(costumer);
    }

    @Override
    public void statusAssign(Costumer costumer){
        if (costumer.getTotalDebt() == 0) {
            costumer.setStatus(PaymentStatus.NULO);
        }
        if (costumer.getDebt() >= 0 && costumer.getTotalDebt() > 0) {
            costumer.setStatus(PaymentStatus.PENDIENTE);
        }
        if (costumer.getDebt() < (costumer.getTotalDebt() / 2)) {
            costumer.setStatus(PaymentStatus.PARCIAL);
        }
        if (costumer.getDebt() == 0 && costumer.getTotalDebt() > 0){
            costumer.setStatus(PaymentStatus.PAGADA);
        }
        costumerRepo.save(costumer);
    }

    @Override
    public void addSaleToCostumer(Costumer costumer) {
        costumer.setDebt(costumer.getTotalDebt());
        costumerRepo.save(costumer);
        statusAssign(costumer);
    }

    @Override
    public void updateTotalDebt(Costumer costumer) {
        costumerRepo.save(costumer);
    }
}
