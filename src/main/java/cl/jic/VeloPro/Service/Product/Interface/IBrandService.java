package cl.jic.VeloPro.Service.Product.Interface;

import cl.jic.VeloPro.Model.Entity.Product.BrandProduct;

import java.util.List;

public interface IBrandService {

    void save(BrandProduct brand);
    List<BrandProduct> getAll();
}
