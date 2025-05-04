package com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem;

import com.dominikdev.ecommerceshop.product.productItem.response.ProductItemResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCartItemResponse {
    private Integer id;
    private ProductItemResponse productItem;
    private Integer qty;
    private String productName;
}
