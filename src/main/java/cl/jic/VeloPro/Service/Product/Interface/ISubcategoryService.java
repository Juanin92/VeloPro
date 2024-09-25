package cl.jic.VeloPro.Service.Product.Interface;

import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.SubcategoryProduct;

import java.util.List;

public interface ISubcategoryService {

    void save(SubcategoryProduct subcategory, CategoryProduct category);
    List<SubcategoryProduct> getAll();
}
