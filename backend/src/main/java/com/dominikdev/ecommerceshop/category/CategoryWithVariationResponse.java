package com.dominikdev.ecommerceshop.category;

import com.dominikdev.ecommerceshop.variation.VariationResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryWithVariationResponse {
    private Integer id;
    private String categoryName;
    private Integer parentCategoryId;
    private List<VariationResponse> variations;
}
