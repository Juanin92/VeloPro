package cl.jic.VeloPro.Service.Sale.Interfaces;

import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;

import java.util.List;
import java.util.Optional;

public interface ISaleService {

    void addSale(Sale sale);
    Long totalSales();
    Optional<Sale> getSaleById(long id);
    List<Sale> getAll();
    int calculateDiscountSale(int total, int amount);
    int calculateTotalSale(List<DetailSaleDTO> dtoList);
    int calculateTax(List<DetailSaleDTO> dtoList);
    int calculateTaxDiscount(List<DetailSaleDTO> dtoList, int discount);
    void saleRegisterVoid(Sale sale);
}
