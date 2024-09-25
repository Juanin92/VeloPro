package cl.jic.VeloPro.Service.Report;

import cl.jic.VeloPro.Model.Entity.Kardex;
import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.MovementsType;
import cl.jic.VeloPro.Repository.KardexRepo;
import cl.jic.VeloPro.Service.Report.Interfaces.IKardexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class KardexService implements IKardexService {
    @Autowired private KardexRepo kardexRepo;

    @Override
    public void addKardex(Product product, int quantity, String comment, MovementsType mov, User user) {
        Kardex kardex = new Kardex();
        kardex.setDate(LocalDate.now());
        kardex.setQuantity(quantity);
        kardex.setStock(product.getStock());
        kardex.setPrice(product.getBuyPrice());
        kardex.setMovementsType(mov);
        kardex.setProduct(product);
        kardex.setUser(user);
        kardex.setComment(comment);
        kardexRepo.save(kardex);
    }

    @Override
    public List<Kardex> getAll() {
        return kardexRepo.findAll();
    }
}
