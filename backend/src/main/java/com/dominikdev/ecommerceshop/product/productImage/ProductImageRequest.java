package com.dominikdev.ecommerceshop.product.productImage;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageRequest {
    private Long id;
    private String imageFilename;
}
