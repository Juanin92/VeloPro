package cl.jic.VeloPro.Service.Purchase.Interfaces;

import cl.jic.VeloPro.Model.DTO.DetailPurchaseDTO;
import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Entity.Purchase.Purchase;
import cl.jic.VeloPro.Model.Entity.Purchase.PurchaseDetail;

import java.util.List;

public interface IPurchaseDetailService {

    void save(DetailPurchaseDTO dto, Purchase purchase, Product product);
    List<PurchaseDetail> getAll();
    DetailPurchaseDTO createDTO(Product product);
    boolean deleteProduct(Long id, List<DetailPurchaseDTO> dto);
}
