package cl.jic.VeloPro.Repository.Customer;

import cl.jic.VeloPro.Model.Entity.Customer.PaymentCustomer;
import cl.jic.VeloPro.Model.Entity.Customer.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCustomerRepo extends JpaRepository<PaymentCustomer,Long> {

    List<PaymentCustomer> findByCustomerId(Long id);
    List<PaymentCustomer> findByDocument(TicketHistory ticket);
}
