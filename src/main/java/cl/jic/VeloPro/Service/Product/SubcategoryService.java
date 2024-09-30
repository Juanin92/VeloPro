package cl.jic.VeloPro.Service.Product;

import cl.jic.VeloPro.Model.Entity.Product.CategoryProduct;
import cl.jic.VeloPro.Model.Entity.Product.SubcategoryProduct;
import cl.jic.VeloPro.Repository.Product.SubcategoryProductRepo;
import cl.jic.VeloPro.Service.Product.Interface.ISubcategoryService;
import cl.jic.VeloPro.Validation.CategoriesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubcategoryService implements ISubcategoryService {

    @Autowired private SubcategoryProductRepo subcategoryProductRepo;
    @Autowired private CategoriesValidator validator;

    @Override
    public void save(SubcategoryProduct subcategory, CategoryProduct category) {
        if (category != null){
            SubcategoryProduct subcategoryProduct = getSubcategoryCreated(capitalize(subcategory.getName()), category.getId());
            if (subcategoryProduct != null){
                throw new IllegalArgumentException("Nombre Existente: Hay registro de esta Subcategoría en la Categoría " + category.getName() + " .");
            } else {
                validator.validateSubcategory(subcategory.getName());
                subcategory.setName(capitalize(subcategory.getName()));
                subcategory.setCategory(category);
                subcategoryProductRepo.save(subcategory);
            }
        }else {
            throw new IllegalArgumentException("Dede seleccionar una categoría.");
        }
    }

    @Override
    public List<SubcategoryProduct> getAll() {
        return subcategoryProductRepo.findAll();
    }

    private SubcategoryProduct getSubcategoryCreated(String name, Long id){
        Optional<SubcategoryProduct> subcategoryProduct = subcategoryProductRepo.findByNameAndCategoryId(name, id);
        return subcategoryProduct.orElse(null);
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
