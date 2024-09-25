package cl.jic.VeloPro.Service.Sale;

import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Repository.Sale.SaleRepo;
import cl.jic.VeloPro.Service.Sale.Interfaces.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService implements ISaleService {

    @Autowired private SaleRepo saleRepo;

    @Override
    public void addSale(Sale sale) {
        sale.setDate(LocalDate.now());
        sale.setDocument("BO_" + totalSales());
        sale.setStatus(true);
        saleRepo.save(sale);
    }

    @Override
    public Long totalSales() {
        return saleRepo.count();
    }

    @Override
    public Optional<Sale> getSaleById(long id) {
        return saleRepo.findById(id);
    }

    @Override
    public List<Sale> getAll() {
        return saleRepo.findAll();
    }

    @Override
    public int calculateDiscountSale(int total, int amount) {
        if (amount >= 0 && amount < 100) {
            return (total * amount) / 100;
        }
        return 0;
    }

    @Override
    public int calculateTotalSale(List<DetailSaleDTO> dtoList) {
        int total = 0;
        for (DetailSaleDTO dto : dtoList) {
            int newTotal = dto.getSalePrice() * dto.getQuantity();
            total += newTotal;
        }
        return total;
    }

    @Override
    public int calculateTax(List<DetailSaleDTO> dtoList) {
        return dtoList.stream().mapToInt(dto -> dto.getTax() * dto.getQuantity()).sum();
    }

    @Override
    public int calculateTaxDiscount(List<DetailSaleDTO> dtoList, int discount) {
        int totalTax = calculateTax(dtoList);
        return totalTax - (totalTax * discount / 100);
    }

    @Override
    public void saleRegisterVoid(Sale sale) {
        sale.setStatus(false);
        if (sale.getComment() == null){
            sale.setComment("ANULADO");
        }else {
            sale.setComment("ANULADO - " + sale.getComment());
        }
        saleRepo.save(sale);
    }
}