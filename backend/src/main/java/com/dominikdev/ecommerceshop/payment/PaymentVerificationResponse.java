package com.dominikdev.ecommerceshop.payment;

import com.dominikdev.ecommerceshop.shopOrder.ShopOrder;
import com.dominikdev.ecommerceshop.userPaymentMethod.UserPaymentMethod;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVerificationResponse {
    private Integer id;
    private String transactionId;
    private String paymentIntentId;
    private ShopOrder shopOrder;
    private UserPaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Payment.PaymentStatus status;

}
