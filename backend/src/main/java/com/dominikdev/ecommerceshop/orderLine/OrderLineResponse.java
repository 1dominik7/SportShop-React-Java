package com.dominikdev.ecommerceshop.orderLine;

import com.dominikdev.ecommerceshop.product.productItem.response.ProductItemToOrderResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLineResponse {
    private Integer id;
    private String productName;
    private ProductItemToOrderResponse productItem;
    private Integer qty;
    private Double price;
}
