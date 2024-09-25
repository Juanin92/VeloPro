package cl.jic.VeloPro.Service.Purchase.Interfaces;

import cl.jic.VeloPro.Model.Entity.Purchase.PurchaseDetail;

import java.util.List;

public interface IPurchaseDetailService {

    void save(PurchaseDetail purchaseDetail);
    List<PurchaseDetail> getAll();
}
