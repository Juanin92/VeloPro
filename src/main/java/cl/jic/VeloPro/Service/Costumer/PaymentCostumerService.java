package cl.jic.VeloPro.Service.Costumer;

import cl.jic.VeloPro.Model.Entity.Costumer.PaymentCostumer;
import cl.jic.VeloPro.Model.Entity.Costumer.TicketHistory;
import cl.jic.VeloPro.Repository.Costumer.PaymentCostumerRepo;
import cl.jic.VeloPro.Service.Costumer.Interface.IPaymentCostumerService;
import cl.jic.VeloPro.Validation.PaymentCostumerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentCostumerService implements IPaymentCostumerService {

    @Autowired private PaymentCostumerRepo paymentCostumerRepo;
    @Autowired private PaymentCostumerValidator validator;

    @Override
    public void addPayments(PaymentCostumer paymentCostumer) {
        validator.validatePayment(String.valueOf(paymentCostumer.getAmount()),paymentCostumer.getComment());
        paymentCostumer.setCostumer(paymentCostumer.getCostumer());
        paymentCostumer.setDate(LocalDate.now());
        paymentCostumer.setDocument(paymentCostumer.getDocument());
        paymentCostumer.setAmount(paymentCostumer.getAmount());
        paymentCostumer.setComment(paymentCostumer.getComment());
        paymentCostumerRepo.save(paymentCostumer);
    }

    @Override
    public List<PaymentCostumer> getAll() {
        return paymentCostumerRepo.findAll();
    }

    @Override
    public List<PaymentCostumer> getCostumerSelected(Long  id) {
        return paymentCostumerRepo.findByCostumerId(id);
    }

    @Override
    public int calculateDebtTicket(TicketHistory ticket) {
        List<PaymentCostumer> pagosDelTicket = paymentCostumerRepo.findByDocument(ticket);
        return pagosDelTicket.stream()
                .mapToInt(PaymentCostumer::getAmount)
                .sum();
    }

    @Override
    public void deletePayments(PaymentCostumer costumer) {
        paymentCostumerRepo.delete(costumer);
    }
}
