package cl.jic.VeloPro.Service.Product.Interface;

import cl.jic.VeloPro.Model.Entity.Product.UnitProduct;

import java.util.List;

public interface IUnitService {

    void save(UnitProduct unit);
    List<UnitProduct> getAll();
}
