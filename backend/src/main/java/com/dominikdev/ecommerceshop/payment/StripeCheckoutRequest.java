package com.dominikdev.ecommerceshop.payment;

import com.dominikdev.ecommerceshop.shopOrder.ShopOrderRequest;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeCheckoutRequest {
    private ShopOrderRequest orderRequest;
    private String successUrl;
    private String cancelUrl;
}
