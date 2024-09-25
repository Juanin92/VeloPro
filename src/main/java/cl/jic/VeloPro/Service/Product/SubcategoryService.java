package cl.jic.VeloPro.Service.Product;

import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.SubcategoryProduct;
import cl.jic.VeloPro.Repository.Product.SubcategoryProductRepo;
import cl.jic.VeloPro.Service.Product.Interface.ISubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubcategoryService implements ISubcategoryService {

    @Autowired private SubcategoryProductRepo subcategoryProductRepo;

    @Override
    public void save(SubcategoryProduct subcategory, CategoryProduct category) {
        if (!subcategory.getName().trim().isBlank() && subcategory.getName().length() >= 3){
            if (!subcategory.getName().matches("[0-9 ]+")){
                if (category != null){
                    subcategory.setName(subcategory.getName().substring(0,1).toUpperCase() + subcategory.getName().substring(1));
                    subcategory.setCategory(category);
                    subcategoryProductRepo.save(subcategory);
                }else {
                    throw new IllegalArgumentException("Dede seleccionar una categoría.");
                }
            }else{
                throw new IllegalArgumentException("El nombre no debe contener dígitos.");
            }
        }else {
            throw new IllegalArgumentException("El campo no debe estar vacío.");
        }
    }

    @Override
    public List<SubcategoryProduct> getAll() {
        return subcategoryProductRepo.findAll();
    }
}
