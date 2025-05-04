package com.dominikdev.ecommerceshop.variation;

import com.dominikdev.ecommerceshop.category.Category;
import com.dominikdev.ecommerceshop.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariationService {

    private final VariationRepository variantRepository;
    private final CategoryRepository categoryRepository;

    public Variation createVariation(VariationRequest variationRequest) {
        Category category = null;
        if (variationRequest.getCategoryId() != null) {
            category = categoryRepository.findById(variationRequest.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        }

        Variation variation = Variation.builder()
                .name(variationRequest.getName())
                .category(category)
                .variationOptions(new ArrayList<>())
                .build();

        return variantRepository.save(variation);
    }

    public List<VariationResponseCatName> getAllVariation() {
        return variantRepository.findAll().stream()
                .map(v ->new VariationResponseCatName(
                        v.getId(),
                        v.getName(),
                        v.getCategory().getCategoryName(),
                        v.getCategory().getId(),
                        v.getVariationOptions()
                )).collect(Collectors.toList());
    }

    public Variation getVariationById(Integer variationId) {
        return variantRepository.findById(variationId).orElseThrow(() -> new RuntimeException("Variation not found with id: " + variationId));
    }

    public List<Variation> getVariationByCategoryId(Integer categoryId) {
        if (categoryId == null) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        return variantRepository.findByCategoryId(categoryId);
    }

    @Transactional
    public Variation updateVariation(Integer variationId, VariationRequest variationRequest) {
        Category category = null;
        Variation variation = variantRepository.findById(variationId).orElseThrow(() -> new RuntimeException("Variation not found with this id: " + variationId));

        if (variationRequest.getCategoryId() != null) {
            category = categoryRepository.findById(variationRequest.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found with this id: " + variationRequest.getCategoryId()));
            variation.setCategory(category);
        } else {
            variation.setCategory(null);
        }

        if (!variationRequest.getName().isEmpty()) {
            variation.setName(variationRequest.getName());
        }

        return variantRepository.save(variation);
    }

    @Transactional
    public void deleteVariation(Integer variationId) {
        Variation variation = variantRepository.findById(variationId).orElseThrow(() -> new RuntimeException("Variation not found with this id: " + variationId));

        Category category = variation.getCategory();
        if (category != null) {
            category.getVariations().remove(variation);

            categoryRepository.save(category);
        }

        variantRepository.delete(variation);
    }
}
