package cl.jic.VeloPro.Service.Customer;

import cl.jic.VeloPro.Model.Entity.Customer.Customer;
import cl.jic.VeloPro.Model.Entity.Customer.PaymentCustomer;
import cl.jic.VeloPro.Model.Entity.Customer.TicketHistory;
import cl.jic.VeloPro.Repository.Customer.PaymentCustomerRepo;
import cl.jic.VeloPro.Service.Customer.Interface.IPaymentCustomerService;
import cl.jic.VeloPro.Validation.PaymentCustomerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentCustomerService implements IPaymentCustomerService {

    @Autowired private PaymentCustomerRepo paymentCustomerRepo;
    @Autowired private PaymentCustomerValidator validator;

    @Override
    public void addPayments(PaymentCustomer paymentCustomer) {
        validator.validatePayment(String.valueOf(paymentCustomer.getAmount()),paymentCustomer.getComment());
        paymentCustomer.setCustomer(paymentCustomer.getCustomer());
        paymentCustomer.setDate(LocalDate.now());
        paymentCustomer.setDocument(paymentCustomer.getDocument());
        paymentCustomer.setAmount(paymentCustomer.getAmount());
        paymentCustomer.setComment(paymentCustomer.getComment());
        paymentCustomerRepo.save(paymentCustomer);
    }

    @Override
    public void addAdjustPayments(int amount, TicketHistory ticket, Customer customer) {
        if (amount > 0) {
            PaymentCustomer paymentCustomer = new PaymentCustomer();
            paymentCustomer.setCustomer(customer);
            paymentCustomer.setDocument(ticket);
            paymentCustomer.setAmount(amount);
            paymentCustomer.setComment("Ajuste");
            addPayments(paymentCustomer);
        }
    }

    @Override
    public List<PaymentCustomer> getAll() {
        return paymentCustomerRepo.findAll();
    }

    @Override
    public List<PaymentCustomer> getCustomerSelected(Long  id) {
        return paymentCustomerRepo.findByCustomerId(id);
    }

    @Override
    public int calculateDebtTicket(TicketHistory ticket) {
        List<PaymentCustomer> paymentTickets = paymentCustomerRepo.findByDocument(ticket);
        return paymentTickets.stream()
                .mapToInt(PaymentCustomer::getAmount)
                .sum();
    }
}
