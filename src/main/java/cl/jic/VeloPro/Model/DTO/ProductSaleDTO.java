package cl.jic.VeloPro.Model.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSaleDTO {
    private Long id;
    private String brand;
    private String description;
    private int total;

    public ProductSaleDTO(Long id, String brand, String description, BigDecimal total) {
        this.id = id;
        this.brand = brand;
        this.description = description;
        this.total = total.intValue();
    }
}
