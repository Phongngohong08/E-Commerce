package com.nhp.ecommerce.services;

import com.nhp.ecommerce.components.LocalizationUtil;
import com.nhp.ecommerce.dtos.ProductDTO;
import com.nhp.ecommerce.dtos.ProductImageDTO;
import com.nhp.ecommerce.exceptions.DataNotFoundException;
import com.nhp.ecommerce.models.Category;
import com.nhp.ecommerce.models.Product;
import com.nhp.ecommerce.models.ProductImage;
import com.nhp.ecommerce.repositories.ProductImageRepository;
import com.nhp.ecommerce.repositories.ProductRepository;
import com.nhp.ecommerce.responses.ProductResponse;
import com.nhp.ecommerce.utils.MessageKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImpProductService implements ProductService{

    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final LocalizationUtil localizationUtil;

    @Override
    public ProductResponse createProduct(ProductDTO productDTO) {
        Category existingCategory = categoryService.getCategoryById(productDTO.getCategoryId());

        Product newProduct = Product.builder().name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        productRepository.save(newProduct);
        return ProductResponse.builder()
                .id(newProduct.getId())
                .name(newProduct.getName())
                .build();
    }

    @Override
    public Product getProductById(Long id) {
        try {
            return productRepository.findProductById(id)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find product with id = " + id));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ProductImageDTO> createProductImage(Long productId, List<MultipartFile> files) {

        Product existingProduct = getProductById(productId);
        files = files == null ? new ArrayList<>() : files;
        if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            try {
                throw new Exception("Maximum images per product is " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        List<ProductImageDTO> productImageDTOS = new ArrayList<>();
        for (MultipartFile file : files) {
            if(file.getSize() == 0) {
                continue;
            }

            if(file.getSize() > 10 * 1024 * 1024) {
                try {
                    throw new Exception("Maximum file size is 10MB");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            String contentType = file.getContentType();
            if(contentType == null || !contentType.startsWith("image/")) {
                try {
                    throw new Exception("Invalid image format");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            String filename = null;
            try {
                filename = storeFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ProductImage productImage = ProductImage.builder().url(filename).product(existingProduct).build();
            ProductImageDTO imageDTO = ProductImageDTO.builder()
                    .productId(existingProduct.getId()).imageUrl(filename)
                    .build();
            productImageDTOS.add(imageDTO);
            productImageRepository.save(productImage);
        }
        return productImageDTOS;
    }

    @Override
    public List<ProductResponse> getPageAndSortProduct(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            productResponses.add(ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .build());
        }
        return productResponses;
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Generate unique filename
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;

        // Create directory if not exists
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Copy file to the target location
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}
