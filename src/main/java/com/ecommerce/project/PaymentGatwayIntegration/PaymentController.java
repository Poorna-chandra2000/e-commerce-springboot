package com.ecommerce.project.PaymentGatwayIntegration;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
//remember to permit all this in filterchain
    private final PaymentService paymentService;

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest orderRequest) throws Exception {
        String orderId = paymentService.createOrder(orderRequest);
        return ResponseEntity.ok(orderId);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentDTO paymentDTO) {
        boolean isVerified = paymentService.verifyPayment(paymentDTO);
        return isVerified ? ResponseEntity.ok("Payment successful") : ResponseEntity.badRequest().body("Payment failed");
    }
}

