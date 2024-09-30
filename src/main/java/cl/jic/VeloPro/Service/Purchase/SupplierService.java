package cl.jic.VeloPro.Service.Purchase;

import cl.jic.VeloPro.Model.Entity.Purchase.Supplier;
import cl.jic.VeloPro.Repository.Purchase.SupplierRepo;
import cl.jic.VeloPro.Service.Purchase.Interfaces.ISupplierService;
import cl.jic.VeloPro.Validation.SupplierValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService implements ISupplierService {

    @Autowired private SupplierRepo supplierRepo;
    @Autowired private SupplierValidator validator;

    @Override
    public void save(Supplier supplier) {
        Supplier supplierDB = getSupplierCreated(supplier.getRut());
        if (supplierDB != null){
            throw new IllegalArgumentException("Proveedor Existente: Hay registro de este proveedor.");
        } else {
            if (supplier.getEmail() == null || supplier.getEmail().isEmpty()) {
                supplier.setEmail("x@x.xxx");
            }
            validator.validate(supplier);
            supplier.setEmail(supplier.getEmail());
            supplier.setName(supplier.getName());
            supplier.setRut(supplier.getRut());
            supplier.setPhone(supplier.getPhone());
            supplierRepo.save(supplier);
        }
    }

    @Override
    public List<Supplier> getAll() {
        return supplierRepo.findAll();
    }

    private Supplier getSupplierCreated(String rut){
        Optional<Supplier> supplier = supplierRepo.findByRut(rut);
        return supplier.orElse(null);
    }
}
