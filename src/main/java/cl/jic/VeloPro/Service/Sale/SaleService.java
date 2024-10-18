package cl.jic.VeloPro.Service.Sale;

import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Costumer.Costumer;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.PaymentMethod;
import cl.jic.VeloPro.Repository.Sale.SaleRepo;
import cl.jic.VeloPro.Service.Costumer.TicketHistoryService;
import cl.jic.VeloPro.Service.Sale.Interfaces.ISaleService;
import javafx.scene.control.TextInputDialog;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService implements ISaleService {

    @Autowired private SaleRepo saleRepo;
    @Autowired private TicketHistoryService ticketHistoryService;

    @Override
    public void addSale(Sale sale) {
        saleRepo.save(sale);
    }

    @Override
    public Sale addSale(int discount, int total, List<DetailSaleDTO> dto, int discountAmount, boolean isSelected,
        Costumer costumer, Long numberSale, int comment, PaymentMethod active){
        Sale sale = new Sale();
        sale.setDiscount(discount);
        sale.setTotalSale(total - discount);

        int totalTax;
        if (isSelected){
            totalTax = calculateTaxDiscount(dto, discountAmount);
        }else {
            totalTax = calculateTax(dto);
        }
        sale.setTax(totalTax);
        configurePaymentMethod(sale, costumer, total, numberSale, comment, active);
        sale.setDate(LocalDate.now());
        sale.setDocument("BO_" + totalSales());
        sale.setStatus(true);
        return sale;
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

    @Override
    public void configurePaymentMethod(Sale sale, Costumer costumer, int total, Long numberSale, int comment, PaymentMethod active) {
        switch (active) {
            case PRESTAMO:
                sale.setPaymentMethod(PaymentMethod.PRESTAMO);
                sale.setCostumer(costumer);
                sale.setComment(null);
                costumer.setTotalDebt(costumer.getDebt() + total);
                ticketHistoryService.AddTicketToCostumer(costumer, numberSale, total, LocalDate.now());
                break;
            case MIXTO:
                sale.setPaymentMethod(PaymentMethod.MIXTO);
                sale.setCostumer(costumer);
                sale.setComment("Abono inicial: $" + comment);
                costumer.setTotalDebt(costumer.getDebt() + total);
                ticketHistoryService.AddTicketToCostumer(costumer, numberSale, total, LocalDate.now());
                break;
            case DEBITO:
                sale.setPaymentMethod(PaymentMethod.DEBITO);
                sale.setCostumer(null);
                break;
            case CREDITO:
                sale.setPaymentMethod(PaymentMethod.CREDITO);
                sale.setCostumer(null);
                break;
            case TRANSFERENCIA:
                sale.setPaymentMethod(PaymentMethod.TRANSFERENCIA);
                sale.setCostumer(null);
                break;
            case EFECTIVO:
                sale.setPaymentMethod(PaymentMethod.EFECTIVO);
                sale.setCostumer(null);
                sale.setComment(null);
                break;
        }
    }

}