package com.dominikdev.ecommerceshop.variation;

import com.dominikdev.ecommerceshop.variation.variationOption.VariationOptionResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariationDTO {
    private String variationName;
    private List<VariationOptionResponse> variationOptions;
}
