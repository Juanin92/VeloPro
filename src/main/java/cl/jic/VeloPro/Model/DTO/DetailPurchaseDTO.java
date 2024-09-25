package cl.jic.VeloPro.Model.DTO;

import lombok.Data;

@Data
public class DetailPurchaseDTO {

    private Long idProduct;
    private String brand;
    private String description;
    private int price;
    private int tax;
    private int quantity;
    private int total;
}
