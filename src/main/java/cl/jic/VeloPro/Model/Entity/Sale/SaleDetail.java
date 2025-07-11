package cl.jic.VeloPro.Model.Entity.Sale;

import cl.jic.VeloPro.Model.Entity.Product.Product;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SaleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private int price;
    private int tax;
    private int total;

    @ManyToOne
    @JoinColumn(name = "id_sale", nullable = false)
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;
}
