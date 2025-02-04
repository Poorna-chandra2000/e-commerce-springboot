package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.payload.ProductResponseDto;
import com.ecommerce.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {
 private final ProductService productService;


 //pagination and sorting on getMapping checkout


    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto,@PathVariable Long categoryId){

        return ResponseEntity.ok(productService.addProduct(productDto,categoryId));
    }

    @GetMapping("public/productDto/getAllProducts")
    public  ResponseEntity<List<ProductDto>> getAllProductsDto(){

        return ResponseEntity.ok(productService.getAllProductsDtos());
    }

    @GetMapping("public/productResponse/getAllProducts")
    public  ResponseEntity<ProductResponseDto> getAllProducts(@RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                              @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
                                                              @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false)String sortBy,
                                                              @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false)String sortOrder){
        ProductResponseDto productResponse=productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("public/productById/{productId}")
    public  ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long productId){
        ProductResponseDto productResponse=productService.getById(productId);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("admin/productById/{productId}")
    public  ResponseEntity<Optional<ProductDto>> getById(@PathVariable Long productId){
        return ResponseEntity.ok(productService.getByIdproductDto(productId));
    }

    @GetMapping("/public/searchProductByCategoryName/{categoryName}")
    public ResponseEntity<ProductResponseDto> getProductsByCategoryName(@PathVariable String categoryName,@RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                        @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
                                                                        @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false)String sortBy,
                                                                        @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false)String sortOrder){
        return ResponseEntity.ok(productService.getProductsByCategoryName(categoryName,pageNumber,pageSize,sortBy,sortOrder));

    }

    //search any productbyname
    @GetMapping("/public/searchProduct/{keyword}")
    public ResponseEntity<ProductResponseDto> getProductsByKeyword(@PathVariable String keyword,
                                                                   @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                   @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
                                                                   @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false)String sortBy,
                                                                   @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false)String sortOrder){
        return ResponseEntity.ok(productService.getProductsByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder));

    }

    @GetMapping("/public/searchProductbyCategoryId/{categoryId}")
    public ResponseEntity<ProductResponseDto> getProductsByCategory(@PathVariable Long categoryId,
                                                                    @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                    @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
                                                                    @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_PRODUCTS_BY,required = false)String sortBy,
                                                                    @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false)String sortOrder){
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder));

    }

    @PutMapping("/admin/updateproduct/{productId}")
    //remember to update on productdto and send response as productdto
    public ResponseEntity<ProductDto> updateproduct(@RequestBody ProductDto productDto, @PathVariable Long productId){
        return ResponseEntity.ok(productService.updateproductByid(productDto,productId));
    }


    @DeleteMapping("/admin/deleteProducts/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        String result = productService.deleteProduct(productId);

        if (result.contains("SUCCESSFULLY")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    //updating images
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDto> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image")MultipartFile image) throws IOException {
        ProductDto updateProductsImage=productService.updateProductImage(productId,image);
        return new ResponseEntity<>(updateProductsImage,HttpStatus.OK);
    }

}


