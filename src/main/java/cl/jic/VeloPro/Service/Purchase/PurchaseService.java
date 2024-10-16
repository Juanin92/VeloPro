package cl.jic.VeloPro.Service.Purchase;

import cl.jic.VeloPro.Model.DTO.DetailPurchaseDTO;
import cl.jic.VeloPro.Model.Entity.Purchase.Purchase;
import cl.jic.VeloPro.Model.Entity.Purchase.Supplier;
import cl.jic.VeloPro.Repository.Purchase.PurchaseRepo;
import cl.jic.VeloPro.Service.Purchase.Interfaces.IPurchaseService;
import cl.jic.VeloPro.Validation.PurchaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseService implements IPurchaseService {

    @Autowired private PurchaseRepo purchaseRepo;
    @Autowired private PurchaseValidator validator;

//    @Override
//    public void save(Purchase purchase) {
//        validator.validate(purchase);
//        purchaseRepo.save(purchase);
//    }

    @Override
    public Purchase save(LocalDate date, Supplier supplier, String documentType, String document, int tax, int total) {
        Purchase purchase =  new Purchase();
        purchase.setDate(date);
        purchase.setSupplier(supplier);
        purchase.setDocumentType(documentType);
        purchase.setDocument(document);
        purchase.setIva(tax);
        purchase.setPurchaseTotal(total);
        validator.validate(purchase);
        purchaseRepo.save(purchase);
        return purchase;
    }

    @Override
    public Long totalPurchase() {
        return purchaseRepo.count();
    }

    @Override
    public int totalPricePurchase(List<DetailPurchaseDTO> dto) {
        return dto.stream()
                .mapToInt(DetailPurchaseDTO::getTotal)
                .sum();
    }
}
