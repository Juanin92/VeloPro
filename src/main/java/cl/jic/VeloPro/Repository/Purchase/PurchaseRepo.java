package cl.jic.VeloPro.Repository.Purchase;

import cl.jic.VeloPro.Model.Entity.Purchase.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepo extends JpaRepository<Purchase, Long> {
}
