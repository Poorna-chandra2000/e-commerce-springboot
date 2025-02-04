package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    Page<Product> findByCategory(Optional<Category> category, Pageable pageDetails);

    Page<Product> findByCategoryOrderByPriceAsc(Optional<Category> category, Pageable pageDetails);

    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageDetails);
}
