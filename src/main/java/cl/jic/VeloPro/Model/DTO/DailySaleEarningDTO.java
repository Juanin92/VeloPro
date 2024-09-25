package cl.jic.VeloPro.Model.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailySaleEarningDTO {
    private LocalDate saleDate;
    private int profit;

    public DailySaleEarningDTO(LocalDate saleDate, BigDecimal profit) {
        this.saleDate = saleDate;
        this.profit = profit != null ? profit.intValue() : 0;
    }
}
