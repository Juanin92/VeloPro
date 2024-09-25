package cl.jic.VeloPro.Validation;

import cl.jic.VeloPro.Model.Entity.Purchase.Purchase;
import cl.jic.VeloPro.Model.Entity.Purchase.Supplier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PurchaseValidator {
    public void validate(Purchase purchase){
        validateDate(purchase.getDate());
        validateSupplier(purchase.getSupplier());
        validateDocument(purchase.getDocument());
        validateTotal(purchase.getPurchaseTotal());
    }

    private void validateDate(LocalDate date){
        if (date == null){
            throw new IllegalArgumentException("Ingrese una fecha");
        }
    }
    private void validateSupplier(Supplier supplier){
        if (supplier == null){
            throw new IllegalArgumentException("Seleccione un proveedor");
        }
    }
    private void validateDocument(String document){
        if (document == null || document.trim().isBlank()){
            throw new IllegalArgumentException("Ingrese Número de Documento.");
        }
    }
    private void validateTotal(int total){
        if (total <= 0){
            throw new IllegalArgumentException("Ingrese sólo números");
        }
    }
}
