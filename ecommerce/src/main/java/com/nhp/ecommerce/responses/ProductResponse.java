package com.nhp.ecommerce.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private String message;
    private Long id;
    private String name;
}
