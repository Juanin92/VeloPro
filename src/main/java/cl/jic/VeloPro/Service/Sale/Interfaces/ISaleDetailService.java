package cl.jic.VeloPro.Service.Sale.Interfaces;

import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Model.Entity.Sale.SaleDetail;
import java.util.List;

public interface ISaleDetailService {

    void createSaleDetails(DetailSaleDTO dto, Sale sale, Product product);
    List<SaleDetail> getAll();
    DetailSaleDTO createDTO(Product product);
    int deleteProduct(List<DetailSaleDTO> dtoList, Long id, int total);
    List<DetailSaleDTO> findDetailSalesBySaleId(Long id);
}
