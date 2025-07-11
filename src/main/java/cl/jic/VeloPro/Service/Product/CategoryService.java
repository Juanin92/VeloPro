package cl.jic.VeloPro.Service.Product;

import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;
import cl.jic.VeloPro.Repository.Product.CategoryProductRepo;
import cl.jic.VeloPro.Service.Product.Interface.ICategoryService;
import cl.jic.VeloPro.Validation.CategoriesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {

    @Autowired private CategoryProductRepo categoryProductRepo;
    @Autowired private CategoriesValidator validator;

    @Override
    public void save(CategoryProduct category) {
        CategoryProduct categoryProduct = getCategoryCreated(capitalize(category.getName()));
        if (categoryProduct != null){
            throw new IllegalArgumentException("Nombre Existente: Hay registro de esta categoría.");
        } else {
            validator.validateCategory(category.getName());
            category.setName(capitalize(category.getName()));
            categoryProductRepo.save(category);
        }
    }

    @Override
    public List<CategoryProduct> getall() {
        return categoryProductRepo.findAll();
    }

    private CategoryProduct getCategoryCreated(String name){
        Optional<CategoryProduct> categoryProduct = categoryProductRepo.findByName(name);
        return categoryProduct.orElse(null);
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String[] words = value.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(word.substring(0, 1).toUpperCase());
                capitalized.append(word.substring(1).toLowerCase());
                capitalized.append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}