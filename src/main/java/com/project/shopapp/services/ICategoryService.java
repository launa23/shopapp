package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO categoryDTO) throws DataNotFoundException;
    Category getCategoryById(long id);
    List<Category> getAllCategories();
    Category updateCategory(long id, CategoryDTO categoryDTO) throws DataNotFoundException;
    void deleteCategory(long id) throws DataNotFoundException;
}
