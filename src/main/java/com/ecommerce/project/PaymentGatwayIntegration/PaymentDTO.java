package com.ecommerce.project.PaymentGatwayIntegration;

import lombok.Data;

import java.util.List;

@Data
public class PaymentDTO {
    private Long payerId;
    private List<ProductDetails> productDetails;
    private String orderId;
    private String paymentId;
    private String signature;


}