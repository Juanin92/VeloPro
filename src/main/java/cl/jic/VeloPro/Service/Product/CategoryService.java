package cl.jic.VeloPro.Service.Product;

import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;
import cl.jic.VeloPro.Repository.Product.CategoryProductRepo;
import cl.jic.VeloPro.Service.Product.Interface.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {

    @Autowired private CategoryProductRepo categoryProductRepo;

    @Override
    public void save(CategoryProduct category) {
        if (!category.getName().trim().isBlank() && category.getName().length() >= 3){
            if (!category.getName().matches("[0-9 ]+")){
                category.setName(category.getName().substring(0,1).toUpperCase() + category.getName().substring(1));
                categoryProductRepo.save(category);
            }else{
                throw new IllegalArgumentException("El nombre no debe contener dígitos.");
            }
        }else{
            throw new IllegalArgumentException("El campo no debe estar vacío.");
        }
    }

    @Override
    public List<CategoryProduct> getall() {
        return categoryProductRepo.findAll();
    }
}
