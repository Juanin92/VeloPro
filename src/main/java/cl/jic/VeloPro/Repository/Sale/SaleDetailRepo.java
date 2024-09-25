package cl.jic.VeloPro.Repository.Sale;

import cl.jic.VeloPro.Model.Entity.Sale.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleDetailRepo extends JpaRepository<SaleDetail, Long> {
}
