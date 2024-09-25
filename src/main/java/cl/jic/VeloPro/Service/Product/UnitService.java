package cl.jic.VeloPro.Service.Product;

import cl.jic.VeloPro.Model.Entity.Product.UnitProduct;
import cl.jic.VeloPro.Repository.Product.UnitProductRepo;
import cl.jic.VeloPro.Service.Product.Interface.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitService implements IUnitService {

    @Autowired private UnitProductRepo unitProductRepo;

    @Override
    public void save(UnitProduct unit) {
        if (!unit.getNameUnit().trim().isBlank()){
            if (unit.getNameUnit().matches("[a-zA-Z0-9 ]+")) {
                if (unit.getNameUnit().matches("[0-9]+ [a-zA-Z]+")) {
                    int digitCount = unit.getNameUnit().replaceAll("[^0-9]", "").length();
                    int letterCount = unit.getNameUnit().replaceAll("[^a-zA-Z]", "").length();

                    if (digitCount <= 2 && letterCount <= 2) {
                        unit.setNameUnit(unit.getNameUnit().substring(0, 4).toUpperCase() + unit.getNameUnit().substring(4));
                        unitProductRepo.save(unit);
                    } else {
                        throw new IllegalArgumentException("El nombre debe tener máximo 2 dígitos y 2 letras.");
                    }
                } else {
                    throw new IllegalArgumentException("El nombre debe tener un espacio entre dígitos y letras.");
                }
            } else {
                throw new IllegalArgumentException("El nombre debe contener solo letras y números.");
            }
        }else {
            throw new IllegalArgumentException("El campo no debe estar vacío.");
        }
    }

    @Override
    public List<UnitProduct> getAll() {
        return unitProductRepo.findAll();
    }
}
