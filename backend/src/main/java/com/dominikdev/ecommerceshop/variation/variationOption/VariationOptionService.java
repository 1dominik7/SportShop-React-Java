package com.dominikdev.ecommerceshop.variation.variationOption;

import com.dominikdev.ecommerceshop.product.productItem.ProductItem;
import com.dominikdev.ecommerceshop.variation.Variation;
import com.dominikdev.ecommerceshop.variation.VariationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VariationOptionService {

    private final VariationOptionRepository variationOptionRepository;
    private final VariationRepository variationRepository;

    public VariationOption createVariation(VariationOptionRequest variationOptionRequest) {
        Variation variation = null;
        if (variationOptionRequest.getVariationId() != null) {
            variation = variationRepository.findById(variationOptionRequest.getVariationId()).orElseThrow(() -> new RuntimeException("Variation not found"));
        }

        VariationOption variationOption = VariationOption.builder()
                .value(variationOptionRequest.getValue())
                .variation(variation)
                .build();

        return variationOptionRepository.save(variationOption);
    }

    public List<VariationOption> getAllVariationOptions() {
        return variationOptionRepository.findAll();
    }

    public VariationOption getVariationOptionById(Integer variationOptionId){
        return variationOptionRepository.findById(variationOptionId).orElseThrow(() -> new RuntimeException("Variation Option not found with this id: "+ variationOptionId));
    }

    public VariationOption updateVariationOption(Integer variationOptionId, VariationOptionRequest variationOptionRequest) {
        VariationOption variationOption = variationOptionRepository.findById(variationOptionId).orElseThrow(() -> new RuntimeException("Variation Option not found"));

        if (variationOptionRequest.getValue() != null) {
            variationOption.setValue(variationOptionRequest.getValue());
        }

        if (variationOptionRequest.getVariationId() != null) {
            Variation variation = variationRepository.findById(variationOptionRequest.getVariationId()).orElseThrow(() -> new RuntimeException("Variation not found"));
            variationOption.setVariation(variation);
        }

        return variationOptionRepository.save(variationOption);
    }

    public void deleteVariationOption(Integer variationOptionId) {
        VariationOption variationOption = variationOptionRepository.findById(variationOptionId).orElseThrow(() -> new RuntimeException("Variation Option not found with id: " + variationOptionId));

        if(variationOption.getVariation() != null){
            variationOption.getVariation().getVariationOptions().remove(variationOption);
        }

        if(variationOption.getProductItems() != null){
            for (ProductItem productItem : variationOption.getProductItems()){
                productItem.getVariationOptions().remove(variationOption);
            }
        }

        variationOptionRepository.delete(variationOption);
    }

}
