package cl.jic.VeloPro.Service.Purchase.Interfaces;

import cl.jic.VeloPro.Model.Entity.Purchase.Supplier;

import java.util.List;

public interface ISupplierService {

    void save(Supplier supplier);
    List<Supplier> getAll();
}
