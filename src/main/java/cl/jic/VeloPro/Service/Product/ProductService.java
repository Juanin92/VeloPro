package cl.jic.VeloPro.Service.Product;

import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Enum.StatusProduct;
import cl.jic.VeloPro.Repository.Product.ProductRepo;
import cl.jic.VeloPro.Service.Product.Interface.IProductService;
import cl.jic.VeloPro.Validation.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired private ProductRepo productRepo;
    @Autowired private ProductValidator validator;

    @Override
    public void save(Product product) {
        validator.validateNewProduct(product);
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.NODISPONIBLE);
        product.setBuyPrice(0);
        product.setSalePrice(0);
        product.setStock(0);
        productRepo.save(product);
    }

    @Override
    public void update(Product product){
        if (product.getStock() > 0){
            product.setStatus(true);
            product.setStatusProduct(StatusProduct.DISPONIBLE);
            productRepo.save(product);
        }else {
            product.setStatus(false);
            product.setStatusProduct(StatusProduct.NODISPONIBLE);
            productRepo.save(product);
        }
    }

    @Override
    public void active(Product product) {
        product.setStatusProduct(StatusProduct.NODISPONIBLE);
        productRepo.save(product);
    }

    @Override
    public void updateStockPurchase(Product product, int price, int quantity) {
        product.setBuyPrice(price);
        product.setStock(product.getStock() + quantity);
        update(product);
    }

    @Override
    public void updateStockSale(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
        update(product);
    }

    @Override
    public void delete(Product product) {
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.DESCONTINUADO);
        productRepo.save(product);
    }

    @Override
    public List<Product> getAll() {
        return productRepo.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }
}
