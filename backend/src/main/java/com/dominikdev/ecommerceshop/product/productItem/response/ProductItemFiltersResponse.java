package com.dominikdev.ecommerceshop.product.productItem.response;

import com.dominikdev.ecommerceshop.variation.VariationResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemFiltersResponse {
    private Integer categoryId;
    private VariationResponse variation;
}
