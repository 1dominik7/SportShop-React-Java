package com.dominikdev.ecommerceshop.payment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {

    private String transactionId;
    private String paymentIntentId;
    private Integer shopOrderId;
    private Integer userPaymentMethodId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Payment.PaymentStatus status;
}
