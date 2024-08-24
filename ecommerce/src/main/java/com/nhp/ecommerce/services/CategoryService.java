package com.nhp.ecommerce.services;

import com.nhp.ecommerce.dtos.CategoryDTO;
import com.nhp.ecommerce.models.Category;

public interface CategoryService {
    Category createCategory(CategoryDTO category);
    Category getCategoryById(long id) throws Exception;
}
