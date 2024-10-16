package cl.jic.VeloPro.Service.Purchase.Interfaces;

import cl.jic.VeloPro.Model.DTO.DetailPurchaseDTO;
import cl.jic.VeloPro.Model.Entity.Purchase.Purchase;
import cl.jic.VeloPro.Model.Entity.Purchase.Supplier;

import java.time.LocalDate;
import java.util.List;

public interface IPurchaseService{

    Purchase save(LocalDate date, Supplier supplier, String documentType, String document, int tax, int total);
    Long totalPurchase();
    int totalPricePurchase(List<DetailPurchaseDTO> dto);
}
