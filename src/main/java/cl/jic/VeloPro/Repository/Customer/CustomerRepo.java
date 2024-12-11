package cl.jic.VeloPro.Repository.Customer;


import cl.jic.VeloPro.Model.Entity.Customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer,Long> {
    Optional<Customer> findByNameAndSurname(String name, String surname);
}
