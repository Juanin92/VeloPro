package cl.jic.VeloPro.Repository.Product;

import cl.jic.VeloPro.Model.Entity.Product.UnitProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitProductRepo extends JpaRepository<UnitProduct, Long> {
    Optional<UnitProduct> findByNameUnit(String name);
}
