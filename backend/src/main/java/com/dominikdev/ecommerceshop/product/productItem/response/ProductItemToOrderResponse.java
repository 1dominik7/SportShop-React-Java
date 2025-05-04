package com.dominikdev.ecommerceshop.product.productItem.response;

import com.dominikdev.ecommerceshop.product.productImage.ProductImage;
import com.dominikdev.ecommerceshop.product.productImage.ProductImageResponse;
import com.dominikdev.ecommerceshop.variation.variationOption.VariationOptionWithVariationResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemToOrderResponse {
    private Integer id;
    private Double price;
    private Integer discount;
    private String productCode;
    private Integer qtyInStock;
    private Integer productId;
    private List<VariationOptionWithVariationResponse> variationOptions;
    private List<ProductImage> productImages;
    private String productName;
    private String productDescription;
}
