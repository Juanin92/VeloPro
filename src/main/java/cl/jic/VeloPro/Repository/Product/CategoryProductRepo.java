package cl.jic.VeloPro.Repository.Product;

import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryProductRepo extends JpaRepository<CategoryProduct, Long> {
    Optional<CategoryProduct> findByName(String name);
}
