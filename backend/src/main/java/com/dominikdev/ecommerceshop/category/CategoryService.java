package com.dominikdev.ecommerceshop.category;

import com.dominikdev.ecommerceshop.exceptions.APIException;
import com.dominikdev.ecommerceshop.variation.Variation;
import com.dominikdev.ecommerceshop.variation.VariationRepository;
import com.dominikdev.ecommerceshop.variation.VariationResponse;
import com.dominikdev.ecommerceshop.variation.variationOption.VariationOptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final VariationRepository variationRepository;
    private final ModelMapper modelMapper;

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {

        if (categoryRequest.getCategoryName() == null || categoryRequest.getCategoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or blank");
        }

        Category category = new Category();
        category.setCategoryName(categoryRequest.getCategoryName());

        if (categoryRequest.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequest.getParentCategoryId()).orElseThrow(() -> new APIException("Parent category not found"));
            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);

        List<Integer> variationIds = savedCategory.getVariations().stream().map(
                variation -> variation.getId()
        ).collect(Collectors.toList());

        CategoryResponse categoryResponse = new CategoryResponse(
                savedCategory.getId(),
                savedCategory.getCategoryName(),
                savedCategory.getParentCategory() != null ? savedCategory.getParentCategory().getId() : null,
                variationIds
        );

        return categoryResponse;
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(category -> {
            List<Variation> variations = variationRepository.findByCategoryId(category.getId());

            List<Integer> variationIds = variations.stream().map(Variation::getId)
                    .collect(Collectors.toList());

            CategoryResponse categoryResponse = modelMapper.map(category, CategoryResponse.class);

            categoryResponse.setVariationIds(variationIds);
            return categoryResponse;
        }).collect(Collectors.toList());
    }

    public List<CategoryWithVariationResponse> getCategoryById(List<Integer> categoryIds) {

        List<Category> categories = categoryRepository.findAllById(categoryIds);

        if (categories.isEmpty()) {
            throw new RuntimeException("Categories not found for the given IDs");
        }

        return categories.stream()
                .map(category -> {
                    CategoryWithVariationResponse categoryResponse = modelMapper.map(category, CategoryWithVariationResponse.class);

                    // Process variations if needed
                    List<VariationResponse> variationResponses = categoryResponse.getVariations().stream()
                            .map(variation -> {
                                List<VariationOptionResponse> options = variation.getOptions().stream()
                                        .map(option -> new VariationOptionResponse(option.getId(), option.getValue()))
                                        .collect(Collectors.toList());

                                return new VariationResponse(variation.getId(), variation.getCategoryId(), variation.getName(), options);
                            })
                            .collect(Collectors.toList());

                    categoryResponse.setVariations(variationResponses);
                    return categoryResponse;
                })
                .collect(Collectors.toList());
    }

    public Category updateCategory(Integer categoryId, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setCategoryName(categoryRequest.getCategoryName());

        if (categoryRequest.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequest.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }
        return categoryRepository.save(category);
    }

    public void deleteCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found!"));

        Category parentCategory = category.getParentCategory();
        if (parentCategory != null) {
            parentCategory.getSubcategories().remove(category);
            categoryRepository.save(parentCategory);
        }

        for (Category subcategory : category.getSubcategories()) {
            subcategory.setParentCategory(null);
            categoryRepository.save(subcategory);
        }
        categoryRepository.delete(category);
    }

}