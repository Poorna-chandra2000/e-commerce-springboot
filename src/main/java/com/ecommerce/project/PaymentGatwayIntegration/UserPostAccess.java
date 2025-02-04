package com.ecommerce.project.PaymentGatwayIntegration;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserPostAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderIdCopy;

    private Long payerId;


    private Long postId;

    private Integer quantity;

    private double originalprice;

    private double price_with_quantity;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
