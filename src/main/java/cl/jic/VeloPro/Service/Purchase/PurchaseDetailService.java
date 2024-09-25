package cl.jic.VeloPro.Service.Purchase;

import cl.jic.VeloPro.Model.Entity.Purchase.PurchaseDetail;
import cl.jic.VeloPro.Repository.Purchase.PurchaseDetailRepo;
import cl.jic.VeloPro.Service.Purchase.Interfaces.IPurchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseDetailService implements IPurchaseDetailService {

    @Autowired private PurchaseDetailRepo purchaseDetailRepo;

    @Override
    public void save(PurchaseDetail purchaseDetail) {
        purchaseDetailRepo.save(purchaseDetail);
    }

    @Override
    public List<PurchaseDetail> getAll() {
        return purchaseDetailRepo.findAll();
    }
}
