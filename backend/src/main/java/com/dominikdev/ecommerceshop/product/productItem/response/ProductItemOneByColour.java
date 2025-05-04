package com.dominikdev.ecommerceshop.product.productItem.response;

import com.dominikdev.ecommerceshop.product.productImage.ProductImage;
import com.dominikdev.ecommerceshop.variation.VariationResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemOneByColour {
    private Integer id;
    private Double price;
    private Integer discount;
    private String productCode;
    private Integer qtyInStock;
    private List<VariationResponse> variations;
    private List<ProductImage> productImages;
    private String productName;
    private String productDescription;
    private Integer productId;
    private Integer categoryId;
    private String colour;
}
