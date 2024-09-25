package cl.jic.VeloPro.Service.Report.Interfaces;

import cl.jic.VeloPro.Model.Entity.Kardex;
import cl.jic.VeloPro.Model.Entity.Product.Product;
import cl.jic.VeloPro.Model.Entity.User;
import cl.jic.VeloPro.Model.Enum.MovementsType;

import java.util.List;

public interface IKardexService {

    void addKardex(Product product, int quantity, String comment, MovementsType mov, User user);
    List<Kardex> getAll();
}
