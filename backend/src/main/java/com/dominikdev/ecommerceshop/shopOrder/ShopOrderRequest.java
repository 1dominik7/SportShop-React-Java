package com.dominikdev.ecommerceshop.shopOrder;

import com.dominikdev.ecommerceshop.address.AddressRequest;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopOrderRequest {

    private Integer userId;
    private LocalDateTime orderDate;
    private AddressRequest addressRequest;
    private Integer shippingMethodId;
    private Double orderTotal;
    private Double finalOrderTotal;
    private Integer cartId;
    private Integer appliedDiscountValue;
}
