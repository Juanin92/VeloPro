package cl.jic.VeloPro.Model.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CategoriesSaleDTO {
    private Long id;
    private String name;
    private int total;

    public CategoriesSaleDTO(Long id, String name, BigDecimal total) {
        this.id = id;
        this.name = name;
        this.total = total.intValue();
    }
}
