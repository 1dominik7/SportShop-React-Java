package com.dominikdev.ecommerceshop.shoppingCart;

import com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem.ShoppingCartItemResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCartResponse {
    private Integer id;
    private List<ShoppingCartItemResponse> shoppingCartItems;
}
