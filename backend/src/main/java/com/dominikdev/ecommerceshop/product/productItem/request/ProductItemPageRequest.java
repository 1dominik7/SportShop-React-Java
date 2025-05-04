package com.dominikdev.ecommerceshop.product.productItem.request;

import com.dominikdev.ecommerceshop.product.productItem.response.ProductItemResponse;
import com.dominikdev.ecommerceshop.variation.VariationResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductItemPageRequest {
    private Integer id;
    private Double price;
    private Integer discount;
    private String productCode;
    private Integer qtyInStock;
    private List<VariationResponse> variations;
    private List<String> productImages;
    private String productName;
    private String productDescription;
    private Integer categoryId;
    private List<ProductItemResponse> otherProductItems;
}