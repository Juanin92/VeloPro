package cl.jic.VeloPro.Service.Purchase.Interfaces;

import cl.jic.VeloPro.Model.Entity.Purchase.Purchase;

public interface IPurchaseService{

    void save(Purchase purchase);
    Long totalPurchase();
}
