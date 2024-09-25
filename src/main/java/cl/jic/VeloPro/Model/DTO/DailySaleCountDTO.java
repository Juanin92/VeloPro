package cl.jic.VeloPro.Model.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailySaleCountDTO {
    private LocalDate date;
    private int sale;

    public DailySaleCountDTO(LocalDate date, Long sale) {
        this.date = date;
        this.sale = sale.intValue();
    }
}
