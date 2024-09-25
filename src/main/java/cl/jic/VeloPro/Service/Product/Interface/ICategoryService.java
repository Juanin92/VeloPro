package cl.jic.VeloPro.Service.Product.Interface;

import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;

import java.util.List;

public interface ICategoryService {

    void save(CategoryProduct category);
    List<CategoryProduct> getall();
}
