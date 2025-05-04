package com.dominikdev.ecommerceshop.product.productItem.response;


import com.dominikdev.ecommerceshop.product.productImage.ProductImage;
import com.dominikdev.ecommerceshop.product.productItem.request.ProductItemRequest;
import com.dominikdev.ecommerceshop.variation.VariationResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemGroupByColorResponse {
    private Integer productId;
    private String productName;
    private Optional<LocalDateTime> createdDate;
    private String colour;
    private List<ProductImage> productImages;
    private List<VariationResponse> variations;
    private List<ProductItemRequest> productItemRequests;
}
