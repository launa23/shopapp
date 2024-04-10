package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    @Override
    public Category createCategory(CategoryDTO categoryDTO) throws DataNotFoundException {
        if (categoryRepository.existsByNameAndActive(categoryDTO.getName(), true)){
            throw new DataNotFoundException("Category's name is already exists");
        }
        Category newCategory = Category.builder()
                .name(categoryDTO.getName())
                .active(true)
                .build();      // Converst từ CategoryDTO -> Category
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findByIdAndActive(id, true).orElse(null);
//                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByIdIsNotAndActive(0, true);
    }

    @Override
    public Category updateCategory(long id, CategoryDTO categoryDTO) throws DataNotFoundException {
        if (categoryRepository.existsByNameAndActive(categoryDTO.getName(), true)){
            throw new DataNotFoundException("Category's name is already exists");
        }
        Category existingCategory = getCategoryById(id);
        if (existingCategory != null){
            existingCategory.setName(categoryDTO.getName());
            categoryRepository.save(existingCategory);
        }
        return existingCategory;
    }

    @Override
    public void deleteCategory(long id) throws DataNotFoundException {
//        Xóa mềm
        Category existingCategory = categoryRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new DataNotFoundException("Cannot find category's id: " + id));
        long id0 = 0;
        Category categoryDefault = categoryRepository.findById(id0)
                .orElseThrow(() -> new DataNotFoundException("Cannot find category's id: 0"));
        List<Product> products = productRepository.findByCategoryId(id);
        for (Product p: products) {
            p.setCategory(categoryDefault);
        }
        if (existingCategory != null){
            existingCategory.setActive(false);
            categoryRepository.save(existingCategory);
        }
    }
}
