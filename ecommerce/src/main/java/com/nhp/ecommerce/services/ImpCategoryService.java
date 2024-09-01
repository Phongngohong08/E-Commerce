package com.nhp.ecommerce.services;

import com.nhp.ecommerce.dtos.CategoryDTO;
import com.nhp.ecommerce.exceptions.DataNotFoundException;
import com.nhp.ecommerce.models.Category;
import com.nhp.ecommerce.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImpCategoryService implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDTO category) {
        Category newCategory = Category
                .builder()
                .name(category.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id){
        try {
            return categoryRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Category not found"));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
