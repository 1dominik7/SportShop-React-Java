package com.dominikdev.ecommerceshop.discountCode;

import com.dominikdev.ecommerceshop.shoppingCart.ShoppingCart;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCodeResponse {

    private Integer id;
    private String name;
    private String code;
    private LocalDateTime expiryDate;
    private Integer discount;
    private Set<ShoppingCart> shoppingCarts;
}
