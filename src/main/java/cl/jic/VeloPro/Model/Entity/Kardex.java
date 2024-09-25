package cl.jic.VeloPro.Model.Entity;

import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Enum.MovementsType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Data
public class Kardex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private LocalDate date;
    private int quantity;
    private int stock;
    private String comment;

    @Enumerated(EnumType.STRING)
    private MovementsType movementsType;
    private int price;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = true)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = true)
    private User user;
}
