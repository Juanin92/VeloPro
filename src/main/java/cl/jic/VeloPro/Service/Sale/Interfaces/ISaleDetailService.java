package cl.jic.VeloPro.Service.Sale.Interfaces;

import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Entity.Sale.SaleDetail;
import java.util.List;

public interface ISaleDetailService {

    void save(SaleDetail saleDetail);
    List<SaleDetail> getAll();
    DetailSaleDTO createDTO(Product product);
}
