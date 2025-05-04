package com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem;

import com.dominikdev.ecommerceshop.product.productItem.request.ProductItemRequest;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCarItemGetProdItemResponse {
    private Integer id;
    private ProductItemRequest productItem;
    private Integer qty;
    private String productName;
}
