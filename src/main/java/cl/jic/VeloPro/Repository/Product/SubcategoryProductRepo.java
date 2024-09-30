package cl.jic.VeloPro.Repository.Product;

import cl.jic.VeloPro.Model.Entity.Product.SubcategoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubcategoryProductRepo extends JpaRepository<SubcategoryProduct, Long> {
    Optional<SubcategoryProduct> findByNameAndCategoryId(String name, Long id);
}
