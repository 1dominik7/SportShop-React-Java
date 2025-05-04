package com.dominikdev.ecommerceshop.variation.variationOption;

import com.dominikdev.ecommerceshop.variation.VariationShortResponse;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariationOptionWithVariationResponse {
    private Integer id;
    private String value;
    private VariationShortResponse variation;
}
