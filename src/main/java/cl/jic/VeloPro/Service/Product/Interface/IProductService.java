package cl.jic.VeloPro.Service.Product.Interface;

import cl.jic.VeloPro.Model.Entity.Product.Product;

import java.util.List;

public interface IProductService {

    void save(Product product);
    List<Product> getAll();
    Product getProductById(Long id);
    void delete(Product product);
    void update(Product product);
    void active(Product product);
    void updateStock(Product product, int price, int quantity);
}
