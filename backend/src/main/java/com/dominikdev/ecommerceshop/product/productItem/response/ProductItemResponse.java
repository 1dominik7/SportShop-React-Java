package com.dominikdev.ecommerceshop.product.productItem.response;

import com.dominikdev.ecommerceshop.product.productImage.ProductImageResponse;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemResponse {
    private Integer id;
    private Double price;
    private Integer discount;
    private String productCode;
    private Integer qtyInStock;
    private List<Integer> variationOptionIds;
    private Set<ProductImageResponse> productImages;
}
