package com.nhp.ecommerce.controllers;

import com.nhp.ecommerce.components.LocalizationUtil;
import com.nhp.ecommerce.dtos.CategoryDTO;
import com.nhp.ecommerce.models.Category;
import com.nhp.ecommerce.responses.ApiResponse;
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
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                        BindingResult result) {

        LOGGER.info("Creating category: {}", categoryDTO);
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();

            return ApiResponse.<CategoryResponse>builder()
                    .message(errorMessages.toString())
                    .build();
        }

        Category category = categoryService.createCategory(categoryDTO);

        return ApiResponse.<CategoryResponse>builder()
                .result(CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .message(localizationUtil.getLocalizedMessage(MessageKey.INSERT_CATEGORY_SUCCESSFULLY))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable long id) {

        Category category = categoryService.getCategoryById(id);

        return ApiResponse.<CategoryResponse>builder()
                .result(CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .message(localizationUtil.getLocalizedMessage(MessageKey.GET_CATEGORY_SUCCESS))
                .build();
    }
}
