package com.ecommerce.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    private String productName;
    private String image;
    private String description;
    private Integer quantity;
    private double price;
    private double discount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(productId, product.productId) && Objects.equals(productName, product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName);
    }

    private double specialPrice;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    //@JsonIgnore
    private Category category;

    //seller with product information
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;

    @OneToMany(mappedBy = "product",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.EAGER)
    private List<CartItem> products=new ArrayList<>();
}
