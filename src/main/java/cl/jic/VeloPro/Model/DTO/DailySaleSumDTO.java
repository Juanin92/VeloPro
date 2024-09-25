package cl.jic.VeloPro.Model.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailySaleSumDTO {
    private LocalDate date;
    private int sum;

    public DailySaleSumDTO(LocalDate date, BigDecimal sum) {
        this.date = date;
        this.sum = sum.intValue();
    }
}
