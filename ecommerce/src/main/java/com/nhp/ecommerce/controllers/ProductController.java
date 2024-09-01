package com.nhp.ecommerce.controllers;

import com.nhp.ecommerce.dtos.ProductDTO;
import com.nhp.ecommerce.dtos.ProductImageDTO;
import com.nhp.ecommerce.models.Product;
import com.nhp.ecommerce.responses.ApiResponse;
import com.nhp.ecommerce.responses.ProductResponse;
import com.nhp.ecommerce.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable long id) {
        LOGGER.info("Getting product with id: {}", id);
        Product product = productService.getProductById(id);
        return ApiResponse.<ProductResponse>builder()
                .result(ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .build())
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int limit) {
        LOGGER.info("Getting products with page: {}, limit: {}", page, limit);
        List<ProductResponse> productResponses = productService.getPageAndSortProduct(page, limit);
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productResponses)
                .build();
    }

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                           BindingResult result) {
        LOGGER.info("Creating product with name: {}", productDTO.getName());
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ApiResponse.<ProductResponse>builder()
                    .message(errorMessages.toString())
                    .build();
        }
        ProductResponse product = productService.createProduct(productDTO);
        return ApiResponse.<ProductResponse>builder()
                .result(ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .build())
                .build();
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<ProductImageDTO>> uploadImages(
            @PathVariable("id") Long productId, @ModelAttribute("files") List<MultipartFile> files) {

        LOGGER.info("Uploading images for product with id: {}", productId);
        List<ProductImageDTO> product = productService.createProductImage(productId, files);

        return ApiResponse.<List<ProductImageDTO>>builder()
                .result(product)
                .build();
    }

    @GetMapping("/images/{imageName}")
    public ApiResponse<ProductImageDTO> viewImage(@PathVariable String imageName) {
        LOGGER.info("Viewing image with name: {}", imageName);
        Path path = Paths.get("uploads").resolve(imageName);
        UrlResource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (Exception e) {
            LOGGER.error("Error viewing image with name: {}", imageName);
            return ApiResponse.<ProductImageDTO>builder()
                    .message("Error viewing image")
                    .build();
        }
        return ApiResponse.<ProductImageDTO>builder()
                .result(ProductImageDTO.builder()
                        .imageUrl(resource.getFilename())
                        .build())
                .build();
    }
}
