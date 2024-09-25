package cl.jic.VeloPro.Service.Purchase;

import cl.jic.VeloPro.Model.Entity.Purchase.Purchase;
import cl.jic.VeloPro.Repository.Purchase.PurchaseRepo;
import cl.jic.VeloPro.Service.Purchase.Interfaces.IPurchaseService;
import cl.jic.VeloPro.Validation.PurchaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService implements IPurchaseService {

    @Autowired private PurchaseRepo purchaseRepo;
    @Autowired private PurchaseValidator validator;

    @Override
    public void save(Purchase purchase) {
        validator.validate(purchase);
        purchaseRepo.save(purchase);
    }

    @Override
    public Long totalPurchase() {
        return purchaseRepo.count();
    }
}
