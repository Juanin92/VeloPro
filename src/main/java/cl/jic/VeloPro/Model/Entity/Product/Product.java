package cl.jic.VeloPro.Model.Entity.Product;

import cl.jic.VeloPro.Model.Entity.Kardex;
import cl.jic.VeloPro.Model.Enum.MovementsType;
import cl.jic.VeloPro.Model.Enum.StatusProduct;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private int salePrice;
    private int buyPrice;
    private int stock;
    private boolean status;
    @Enumerated(EnumType.STRING)
    private StatusProduct statusProduct;

    @ManyToOne
    @JoinColumn(name = "id_brand", nullable = false)
    private BrandProduct brand;

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private CategoryProduct category;

    @ManyToOne
    @JoinColumn(name = "id_unit", nullable = false)
    private UnitProduct unit;

    @ManyToOne
    @JoinColumn(name = "id_subcategory", nullable = false)
    private SubcategoryProduct subcategoryProduct;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Kardex> kardexList = new ArrayList<>();
}
