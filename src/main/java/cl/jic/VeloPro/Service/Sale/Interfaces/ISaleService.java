package cl.jic.VeloPro.Service.Sale.Interfaces;

import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Customer.Customer;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Model.Enum.PaymentMethod;

import java.util.List;
import java.util.Optional;

public interface ISaleService {

    void addSale(Sale sale);
    Sale addSale(int discount, int total, List<DetailSaleDTO> dto, int discountAmount, boolean isSelected,
                 Customer customer, Long numberSale, int comment, PaymentMethod active);
    Long totalSales();
    Optional<Sale> getSaleById(long id);
    List<Sale> getAll();
    int calculateDiscountSale(int total, int amount);
    int calculateTotalSale(List<DetailSaleDTO> dtoList);
    int calculateTax(List<DetailSaleDTO> dtoList);
    int calculateTaxDiscount(List<DetailSaleDTO> dtoList, int discount);
    void saleRegisterVoid(Sale sale);
    void configurePaymentMethod(Sale sale, Customer customer, int total, Long numberSale, int comment, PaymentMethod active);
}
