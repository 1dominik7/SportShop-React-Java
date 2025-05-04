package com.dominikdev.ecommerceshop.variation;

import com.dominikdev.ecommerceshop.variation.variationOption.VariationOptionResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariationResponse {
    private Integer id;
    private Integer categoryId;
    private String name;
    private List<VariationOptionResponse> options;
}
