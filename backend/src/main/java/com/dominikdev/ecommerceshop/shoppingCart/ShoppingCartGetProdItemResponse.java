package com.dominikdev.ecommerceshop.shoppingCart;

import com.dominikdev.ecommerceshop.discountCode.DiscountCode;
import com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem.ShoppingCarItemGetProdItemResponse;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingCartGetProdItemResponse {
        private Integer id;
        private List<ShoppingCarItemGetProdItemResponse> shoppingCartItems;
        private Set<DiscountCode> discountCodes;
}
