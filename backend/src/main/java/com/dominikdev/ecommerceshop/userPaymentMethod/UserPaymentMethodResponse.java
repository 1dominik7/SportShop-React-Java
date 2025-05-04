package com.dominikdev.ecommerceshop.userPaymentMethod;

import com.dominikdev.ecommerceshop.payment.Payment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPaymentMethodResponse {

    private Integer id;
    private Integer UserId;
    private String paymentTypeName;
    private String provider;
    private String last4CardNumber;
    private LocalDateTime paymentDate;
    private LocalDateTime expiryDate;
    private boolean isDefault;
    private List<Payment> payments;

}
