package com.dominikdev.ecommerceshop.product.productItem.response;
import com.dominikdev.ecommerceshop.product.productImage.ProductImage;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemOneByColourResponse {
    private Integer productId;
    private String productName;
    private String colour;
    private List<ProductItemOneByColour> productItemOneByColour;
    private List<OtherProductItemOneByColour> otherProductItemOneByColours;
    private List<ProductImage> productImages;
}