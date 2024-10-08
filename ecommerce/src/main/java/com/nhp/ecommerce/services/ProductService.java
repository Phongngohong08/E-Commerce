package com.nhp.ecommerce.services;

import com.nhp.ecommerce.dtos.ProductDTO;
import com.nhp.ecommerce.dtos.ProductImageDTO;
import com.nhp.ecommerce.models.Product;
import com.nhp.ecommerce.responses.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductDTO productDTO);
    Product getProductById(Long id);
    List<ProductImageDTO> createProductImage(Long productId, List<MultipartFile> files);
    List<ProductResponse> getPageAndSortProduct(int page, int size);
}
