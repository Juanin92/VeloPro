package cl.jic.VeloPro.Model.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailySaleAvgDTO {
    private LocalDate date;
    private int avg;

    public DailySaleAvgDTO(LocalDate date, BigDecimal avg) {
        this.date = date;
        this.avg = avg.intValue();
    }
}
