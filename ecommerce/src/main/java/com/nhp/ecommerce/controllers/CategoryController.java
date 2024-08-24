package com.nhp.ecommerce.controllers;

import com.nhp.ecommerce.components.LocalizationUtil;
import com.nhp.ecommerce.dtos.CategoryDTO;
import com.nhp.ecommerce.models.Category;
import com.nhp.ecommerce.responses.CategoryResponse;
import com.nhp.ecommerce.services.CategoryService;
import com.nhp.ecommerce.utils.MessageKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/categories")
public class CategoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService categoryService;
    private final LocalizationUtil localizationUtil;

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult result) {
        CategoryResponse categoryResponse = new CategoryResponse();
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            categoryResponse.setMessage(localizationUtil.getLocalizedMessage(MessageKey.INSERT_CATEGORY_FAILED));
            categoryResponse.setErrors(errorMessages);
            return ResponseEntity.badRequest().body(categoryResponse);
        }
        Category category = categoryService.createCategory(categoryDTO);
        categoryResponse.setCategory(category);
        return ResponseEntity.ok(categoryResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable long id) {
        try {
            LOGGER.info("Getting category with id: {}", id);
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
