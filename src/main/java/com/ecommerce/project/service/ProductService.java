package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.payload.ProductResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductDto addProduct(ProductDto product, Long categoryId);


    ProductResponseDto getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponseDto getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ProductResponseDto getProductsByCategoryName(String categoryName, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponseDto getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);


    ProductDto updateproductByid(ProductDto productDto, Long productId);

    String deleteProduct(Long productId);

    List<ProductDto> getAllProductsDtos();

    ProductResponseDto getById(Long productId);

    Optional<ProductDto> getByIdproductDto(Long productId);

    ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException;
}
