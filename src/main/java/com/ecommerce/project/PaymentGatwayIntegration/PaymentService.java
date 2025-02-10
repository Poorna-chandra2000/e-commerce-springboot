package com.ecommerce.project.PaymentGatwayIntegration;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    @Value("${razorpay.key}")
    private String RAZORPAY_KEY;

    @Value("${razorpay.secret}")
    private String RAZORPAY_SECRET;

    //use auto wired or Required args constructor
    private final UserPostAccessRepository userPostAccessRepository;
    //=============================================================================================================
    private RazorpayClient razorpayClient;

    // Initialize RazorpayClient after fields are injected=======================================
    //or configuration is done here instead of seperate razorpayconfig
    @PostConstruct
    public void init() {
        try {
            razorpayClient = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);
        } catch (Exception e) {
            log.error("Failed to initialize RazorpayClient", e);
            throw new RuntimeException("Failed to initialize RazorpayClient", e);
        }
    }
    //===========================================================================================================

    public String createOrder(OrderRequest orderRequest) throws Exception {
        JSONObject optionspay = new JSONObject();
        optionspay.put("amount", (int) (orderRequest.getAmount() * 100)); // Convert to paise
        optionspay.put("currency", orderRequest.getCurrency());
        optionspay.put("receipt", "order_rcpt_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(optionspay);
        return order.get("id"); // Return Razorpay order ID
    }

    public boolean verifyPayment(PaymentDTO paymentDTO) {
        try {
            // Verify the signature using Razorpay's utility
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", paymentDTO.getOrderId());
            options.put("razorpay_payment_id", paymentDTO.getPaymentId());
            options.put("razorpay_signature", paymentDTO.getSignature());

            Utils.verifyPaymentSignature(options, RAZORPAY_SECRET);

            // Save user-post access if payment is verified
            if (paymentDTO.getSignature() != null && !paymentDTO.getSignature().isEmpty()) {
                log.info("Payment Successful");
                List<UserPostAccess> accesses = paymentDTO.getProductDetails().stream().map(product -> {
                    UserPostAccess access = new UserPostAccess();
                    access.setPayerId(paymentDTO.getPayerId());
                    access.setPostId(product.getId());
                    access.setOriginalprice(product.getPrice());
                    access.setQuantity(product.getQuantity());
                    access.setOrderIdCopy(paymentDTO.getOrderId());
                    access.setPrice_with_quantity(product.getQuantity()* product.getPrice());
                    access.setPaymentStatus(PaymentStatus.PAID);
                    return access;
                }).toList();

                userPostAccessRepository.saveAll(accesses);
                return true;
            }
        } catch (Exception e) {
            log.error("Payment verification failed", e);
        }
        return false;
    }
}
