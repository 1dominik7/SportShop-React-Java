package com.dominikdev.ecommerceshop.variation;

import com.dominikdev.ecommerceshop.variation.variationOption.VariationOption;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariationResponseCatName {

    private Integer id;
    private String name;
    private String categoryName;
    private Integer categoryId;
    private List<VariationOption> variationOptions;
}
