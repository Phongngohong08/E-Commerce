package com.nhp.ecommerce.responses;

import com.nhp.ecommerce.models.Category;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {

    private String message;
    private List<String> errors;
    private Category category;
}
