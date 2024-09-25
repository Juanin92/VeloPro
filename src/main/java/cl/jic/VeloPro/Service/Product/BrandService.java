package cl.jic.VeloPro.Service.Product;

import cl.jic.VeloPro.Model.Entity.Product.BrandProduct;
import cl.jic.VeloPro.Repository.Product.BrandProductRepo;
import cl.jic.VeloPro.Service.Product.Interface.IBrandService;
import cl.jic.VeloPro.Validation.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService implements IBrandService {

    @Autowired private BrandProductRepo brandProductRepo;
    @Autowired private ProductValidator validator;

    @Override
    public void save(BrandProduct brand) {
        if (!brand.getName().trim().isBlank() && brand.getName().trim().length() >= 2){
            brand.setName(brand.getName().substring(0,1).toUpperCase() + brand.getName().substring(1));
            brandProductRepo.save(brand);
        }else {
            throw new IllegalArgumentException("El campo no debe estar vacío.");
        }
    }

    @Override
    public List<BrandProduct> getAll() {
        return brandProductRepo.findAll();
    }
}
