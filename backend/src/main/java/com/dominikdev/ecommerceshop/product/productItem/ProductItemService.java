package com.dominikdev.ecommerceshop.product.productItem;

import com.dominikdev.ecommerceshop.product.Product;
import com.dominikdev.ecommerceshop.product.ProductRepository;
import com.dominikdev.ecommerceshop.product.productImage.ProductImage;
import com.dominikdev.ecommerceshop.product.productImage.ProductImageRepository;
import com.dominikdev.ecommerceshop.product.productItem.request.CreateProductItemRequest;
import com.dominikdev.ecommerceshop.product.productItem.request.ProductItemPageRequest;
import com.dominikdev.ecommerceshop.product.productItem.request.ProductItemRequest;
import com.dominikdev.ecommerceshop.product.productItem.response.*;
import com.dominikdev.ecommerceshop.variation.Variation;
import com.dominikdev.ecommerceshop.variation.VariationResponse;
import com.dominikdev.ecommerceshop.variation.variationOption.VariationOption;
import com.dominikdev.ecommerceshop.variation.variationOption.VariationOptionRepository;
import com.dominikdev.ecommerceshop.variation.variationOption.VariationOptionResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductItemService {

    private final ProductItemRepository productItemRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final VariationOptionRepository variationOptionRepository;

    public ProductItem createProductItem(CreateProductItemRequest createProductItemRequest) {
        Product product = productRepository.findById(createProductItemRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product with this id does not exist."));

        ProductItem newProductItem = new ProductItem();
        newProductItem.setPrice(createProductItemRequest.getPrice());
        newProductItem.setDiscount(createProductItemRequest.getDiscount());
        newProductItem.setProductCode(createProductItemRequest.getProductCode());
        newProductItem.setQtyInStock(createProductItemRequest.getQtyInStock());
        newProductItem.setProduct(product);

        List<VariationOption> variationOptions = variationOptionRepository
                .findAllById(createProductItemRequest.getVariationOptionIds());
        newProductItem.setVariationOptions(variationOptions);

        if (createProductItemRequest.getProductImages() != null) {
            List<ProductImage> productImages = new ArrayList<>();
            for (String imageUrl : createProductItemRequest.getProductImages()) {
                Optional<ProductImage> existingImage = productImageRepository.findByImageFilename(imageUrl);

                ProductImage productImage;
                if (existingImage.isPresent()) {
                    productImage = existingImage.get();
                } else {
                    productImage = new ProductImage();
                    productImage.setImageFilename(imageUrl);
                    productImageRepository.save(productImage);
                }

                productImages.add(productImage);
                productImage.getProductItems().add(newProductItem);
            }
            newProductItem.setProductImages(productImages);
        }

        return productItemRepository.save(newProductItem);
    }

    public ProductItemDTO getProductItems(
            Integer categoryId,
            List<Integer> variationIds,
            List<Integer> variationOptionIds,
            Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        List<ProductItem> productPage;

        if (categoryId != null) {
            if (variationIds != null && !variationIds.isEmpty() && variationOptionIds != null && !variationOptionIds.isEmpty()) {
                productPage = productItemRepository.findByFilters(categoryId, variationIds, variationOptionIds);
            } else {
                productPage = productItemRepository.findByCategoryId(categoryId);
            }
        } else {
            productPage = productItemRepository.findAll();
        }

        if ("productId".equals(sortBy)) {
            productPage.sort(Comparator.comparing(productItem -> productItem.getProduct().getId()));
        } else if ("id".equals(sortBy)) {
            productPage.sort(Comparator.comparing(ProductItem::getId));
        } else if ("price".equals(sortBy)) {
            productPage.sort(Comparator.comparing(ProductItem::getPrice));
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            Collections.reverse(productPage);
        }

        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, productPage.size());
        List<ProductItem> pagedProductItems = productPage.subList(start, end);

        List<ProductItemRequest> productItemRequests = pagedProductItems.stream()
                .map(this::mapToProductItemRequest)
                .collect(Collectors.toList());

        int totalElements = productPage.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return new ProductItemDTO(
                productItemRequests,
                pageNumber,
                pageSize,
                (long) totalElements,
                totalPages,
                pageNumber >= totalPages - 1
        );
    }

    private ProductItemRequest mapToProductItemRequest(ProductItem productItem) {

        return ProductItemRequest.builder()
                .id(productItem.getId())
                .price(productItem.getPrice())
                .discount(productItem.getDiscount())
                .productCode(productItem.getProductCode())
                .qtyInStock(productItem.getQtyInStock())
                .productName(productItem.getProduct().getProductName())
                .productId(productItem.getProduct().getId())
                .productImages(productItem.getProductImages())
                .variations(productItem.getVariationOptions().stream()
                        .collect(Collectors.groupingBy(option -> option.getVariation().getId()))
                        .entrySet().stream()
                        .map(entry -> {
                            Variation variation = entry.getValue().get(0).getVariation();
                            VariationResponse variationResponse = new VariationResponse();
                            variationResponse.setId(variation.getId());
                            variationResponse.setCategoryId(variation.getCategory() != null ? variation.getCategory().getId() : null);
                            variationResponse.setName(variation.getName());

                            variationResponse.setOptions(entry.getValue().stream()
                                    .map(option -> {
                                        VariationOptionResponse optionResponse = new VariationOptionResponse();
                                        optionResponse.setId(option.getId());
                                        optionResponse.setValue(option.getValue());
                                        return optionResponse;
                                    })
                                    .collect(Collectors.toList()));
                            return variationResponse;
                        }).collect(Collectors.toList()))
                .build();
    }

    public ProductItemGroupByColourDTO getGroupedProductsByColour(Integer categoryId,
                                                                  List<Integer> variationIds,
                                                                  List<Integer> variationOptionIds,
                                                                  Integer pageNumber,
                                                                  Integer pageSize,
                                                                  String sortBy,
                                                                  String sortOrder,
                                                                  Optional<Integer> limit
    ) {

        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String effectiveSortBy = (sortBy == null || sortBy.isEmpty()) ? "createdDate" : sortBy;

        Pageable pageable = limit.isPresent()
                ? PageRequest.of(0, limit.get(), Sort.by(direction, "createdDate"))
                : PageRequest.of(pageNumber, pageSize, Sort.by(direction, effectiveSortBy));

        List<ProductItem> productPage;

        if (categoryId != null) {
            if (variationIds != null && !variationIds.isEmpty() && variationOptionIds != null && !variationOptionIds.isEmpty()) {
                productPage = productItemRepository.findByFilters(categoryId, variationIds, variationOptionIds);
            } else {
                productPage = productItemRepository.findByCategoryId(categoryId);
            }
        } else {
            productPage = productItemRepository.findAll();
        }

        List<ProductItemRequest> productItemRequests = productPage.stream()
                .map(this::mapToProductItemRequestGrouped)
                .collect(Collectors.toList());

        Map<Integer, Map<String, List<ProductItemRequest>>> groupedByProductAndColour = productItemRequests.stream()
                .filter(item -> item.getColour() != null && !item.getColour().isEmpty())
                .collect(Collectors.groupingBy(ProductItemRequest::getProductId,
                        Collectors.groupingBy(ProductItemRequest::getColour)));

        List<ProductItemGroupByColorResponse> response = groupedByProductAndColour.entrySet().stream()
                .map(productEntry -> {
                    Integer productId = productEntry.getKey();
                    Map<String, List<ProductItemRequest>> colourGroups = productEntry.getValue();

                    return colourGroups.entrySet().stream()
                            .map(colourEntry -> {
                                String colour = colourEntry.getKey();
                                List<ProductItemRequest> requests = colourEntry.getValue();

                                ProductItemGroupByColorResponse group = new ProductItemGroupByColorResponse();
                                group.setProductId(productId);
                                group.setProductName(requests.get(0).getProductName());
                                group.setColour(colour);
                                group.setProductItemRequests(requests);

                                group.setProductImages(getProductImagesForColour(requests, colour));

                                group.setVariations(
                                        requests.stream()
                                                .flatMap(req -> req.getVariations().stream())
                                                .collect(Collectors.toMap(
                                                        VariationResponse::getId,
                                                        v -> {
                                                            VariationResponse newVar = new VariationResponse();
                                                            newVar.setId(v.getId());
                                                            newVar.setCategoryId(v.getCategoryId());
                                                            newVar.setName(v.getName());

                                                            newVar.setOptions(new ArrayList<>(v.getOptions()));
                                                            return newVar;
                                                        },
                                                        (existing, newVar) -> {
                                                            Set<String> existingOptionValues = existing.getOptions().stream()
                                                                    .map(VariationOptionResponse::getValue)
                                                                    .collect(Collectors.toSet());

                                                            newVar.getOptions().stream()
                                                                    .filter(opt -> !existingOptionValues.contains(opt.getValue()))
                                                                    .forEach(existing.getOptions()::add);

                                                            return existing;
                                                        }
                                                ))
                                                .values()
                                                .stream()
                                                .collect(Collectors.toList())
                                );

                                return group;
                            })
                            .collect(Collectors.toList());
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());


        if ("productId".equals(sortBy)) {
            response.sort(Comparator.comparing(ProductItemGroupByColorResponse::getProductId));
        } else if ("id".equals(sortBy)) {
            response.sort(Comparator.comparing(
                    (ProductItemGroupByColorResponse group) -> group.getProductItemRequests().stream() // Jawnie określ typ
                            .mapToInt(ProductItemRequest::getId)
                            .min()
                            .orElse(Integer.MAX_VALUE),
                    "desc".equalsIgnoreCase(sortOrder) ? Comparator.reverseOrder() : Comparator.naturalOrder()
            ));
        } else if ("price".equals(sortBy)) {
            response.sort(Comparator.comparing(group -> group.getProductItemRequests().stream()
                    .mapToDouble(ProductItemRequest::getPrice)
                    .min()
                    .orElse(0)));
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            Collections.reverse(response);
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), response.size());
        List<ProductItemGroupByColorResponse> pagedGroups = response.subList(start, end);

        int totalElements = response.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        ProductItemGroupByColourDTO result = new ProductItemGroupByColourDTO();
        result.setContent(pagedGroups);
        result.setPageNumber(pageNumber);
        result.setPageSize(pageSize);
        result.setTotalElements(totalElements);
        result.setTotalPages(totalPages);
        result.setLastPage(pageNumber >= totalPages - 1);

        return result;
    }

    private List<ProductImage> getProductImagesForColour(List<ProductItemRequest> productItems, String colour) {
        List<ProductImage> allImages = new ArrayList<>();

        for (ProductItemRequest request : productItems) {
            if (request.getColour().equalsIgnoreCase(colour)) {
                allImages.addAll(request.getProductImages());
            }
        }
        allImages.sort(Comparator.comparing(ProductImage::getId));
        return allImages;
    }

    private ProductItemRequest mapToProductItemRequestGrouped(ProductItem productItem) {
        String colour = productItem.getVariationOptions().stream()
                .filter(option -> option.getVariation().getName().equalsIgnoreCase("colour"))
                .map(VariationOption::getValue)
                .findFirst()
                .orElse("Unknown");

        String size = productItem.getVariationOptions().stream()
                .filter(option -> option.getVariation().getName().equalsIgnoreCase("size"))
                .map(VariationOption::getValue)
                .findFirst()
                .orElse("Unknown");

        return ProductItemRequest.builder()
                .id(productItem.getId())
                .price(productItem.getPrice())
                .discount(productItem.getDiscount())
                .productCode(productItem.getProductCode())
                .qtyInStock(productItem.getQtyInStock())
                .productName(productItem.getProduct().getProductName())
                .productId(productItem.getProduct().getId())
                .colour(colour)
                .size(size)
                .productImages(productItem.getProductImages())
                .variations(productItem.getVariationOptions().stream()
                        .collect(Collectors.groupingBy(option -> option.getVariation().getId()))
                        .entrySet().stream()
                        .map(entry -> {
                            Variation variation = entry.getValue().get(0).getVariation();
                            VariationResponse variationResponse = new VariationResponse();
                            variationResponse.setId(variation.getId());
                            variationResponse.setCategoryId(variation.getCategory() != null ? variation.getCategory().getId() : null);
                            variationResponse.setName(variation.getName());

                            variationResponse.setOptions(entry.getValue().stream()
                                    .map(option -> {
                                        VariationOptionResponse optionResponse = new VariationOptionResponse();
                                        optionResponse.setId(option.getId());
                                        optionResponse.setValue(option.getValue());
                                        return optionResponse;
                                    })
                                    .collect(Collectors.toList()));
                            return variationResponse;
                        }).collect(Collectors.toList()))
                .build();
    }

    public List<ProductItemFiltersResponse> getProductItemFilters(Integer categoryId, List<Integer> variationIds, List<Integer> variationOptionIds, Optional<Integer> limit) {
        ProductItemGroupByColourDTO groupedProducts = getGroupedProductsByColour(
                categoryId, variationIds, variationOptionIds, 0, Integer.MAX_VALUE, "productId", "asc",limit);

        Map<Integer, VariationResponse> variationsMap = new HashMap();

        for (ProductItemGroupByColorResponse group : groupedProducts.getContent()) {
            for (ProductItemRequest request : group.getProductItemRequests()) {
                for (VariationResponse variationResponse : request.getVariations()) {
                    VariationResponse variation = variationsMap.computeIfAbsent(variationResponse.getId(), id -> {
                        VariationResponse newVar = new VariationResponse();
                        newVar.setId(variationResponse.getId());
                        newVar.setName(variationResponse.getName());
                        newVar.setCategoryId(variationResponse.getCategoryId());
                        newVar.setOptions(new ArrayList<>());
                        return newVar;
                    });

                    for (VariationOptionResponse optionResponse : variationResponse.getOptions()) {
                        if (variation.getOptions().stream().noneMatch(opt -> opt.getId().equals(optionResponse.getId()))) {
                            variation.getOptions().add(optionResponse);
                        }
                    }
                }
            }
        }

        List<ProductItemFiltersResponse> filtersResponse = new ArrayList<>();

        for (VariationResponse variation : variationsMap.values()) {
            ProductItemFiltersResponse response = new ProductItemFiltersResponse();
            response.setCategoryId(categoryId);
            response.setVariation(variation);
            filtersResponse.add(response);
        }

        return filtersResponse;
    }

    public ProductItemOneByColourResponse getProductItemById(Integer productId, String colour) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<ProductItem> productItems = productItemRepository.findByProductId(productId);

        List<ProductItem> filteredItems = productItems.stream()
                .filter(item -> extractColour(item).equalsIgnoreCase(colour))
                .toList();

        if (filteredItems.isEmpty()) {
            return null;
        }

        List<ProductItemOneByColour> productItemOneByColours = filteredItems.stream()
                .map(this::mapToProductItemOneByColour)
                .toList();

        List<OtherProductItemOneByColour> otherColours = getOtherColours(productItems, colour);

        return ProductItemOneByColourResponse.builder()
                .productId(productId)
                .productName(filteredItems.get(0).getProduct().getProductName())
                .colour(colour)
                .productItemOneByColour(productItemOneByColours)
                .otherProductItemOneByColours(otherColours)
                .productImages(getImagesForColour(filteredItems))
                .build();
    }

    public List<ProductItemOneByColour> getProductItemByIds(List<Integer> productItemIds) {
        List<ProductItem> productItems = productItemRepository.findAllById(productItemIds);

        if (productItems.isEmpty()) {
            throw new RuntimeException("No product items found with given IDs");
        }

        return productItems.stream().map(this::mapToProductItemOneByColour)
                .collect(Collectors.toList());
    }

    private List<ProductImage> getImagesForColour(List<ProductItem> filteredItems) {
        return filteredItems.stream()
                .flatMap(item -> item.getProductImages().stream())
                .distinct()
                .sorted(Comparator.comparing(ProductImage::getId))
                .collect(Collectors.toList());
    }

    private String extractColour(ProductItem productItem) {
        return productItem.getVariationOptions().stream()
                .filter(option -> option.getVariation() != null && "colour".equalsIgnoreCase(option.getVariation().getName()))
                .map(VariationOption::getValue)
                .findFirst()
                .orElse("Unknown");
    }


    private ProductItemOneByColour mapToProductItemOneByColour(ProductItem item) {
        return ProductItemOneByColour.builder()
                .id(item.getId())
                .price(item.getPrice())
                .discount(item.getDiscount())
                .productCode(item.getProductCode())
                .qtyInStock(item.getQtyInStock())
                .variations(item.getVariationOptions().stream()
                        .collect(Collectors.groupingBy(option -> option.getVariation().getId()))
                        .entrySet().stream()
                        .map(entry -> {
                            Variation variation = entry.getValue().get(0).getVariation();
                            VariationResponse variationResponse = new VariationResponse();
                            variationResponse.setId(variation.getId());
                            variationResponse.setCategoryId(variation.getCategory() != null ? variation.getCategory().getId() : null);
                            variationResponse.setName(variation.getName());

                            variationResponse.setOptions(entry.getValue().stream()
                                    .map(option -> {
                                        VariationOptionResponse optionResponse = new VariationOptionResponse();
                                        optionResponse.setId(option.getId());
                                        optionResponse.setValue(option.getValue());
                                        return optionResponse;
                                    })
                                    .collect(Collectors.toList()));
                            return variationResponse;
                        }).collect(Collectors.toList()))
                .productImages(item.getProductImages())
                .productName(item.getProduct().getProductName())
                .productDescription(item.getProduct().getDescription())
                .productId(item.getProduct().getId())
                .categoryId(item.getProduct().getCategory().getId())
                .colour(extractColour(item))
                .build();
    }

    private List<OtherProductItemOneByColour> getOtherColours(List<ProductItem> allItems, String currentColour) {

        Map<String, Set<ProductImage>> colourImagesMap = allItems.stream()
                .filter(item -> !extractColour(item).equalsIgnoreCase(currentColour))
                .collect(Collectors.groupingBy(
                        item -> extractColour(item),
                        Collectors.flatMapping(
                                item -> item.getProductImages().stream(),
                                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ProductImage::getId)))
                        )
                ));

        return colourImagesMap.entrySet().stream()
                .map(entry -> {
                    String colour = entry.getKey();
                    Set<ProductImage> uniqueImages = entry.getValue();

                    return OtherProductItemOneByColour.builder()
                            .productId(allItems.get(0).getProduct().getId())
                            .productName(allItems.get(0).getProduct().getProductName())
                            .colour(colour)
                            .productImages(uniqueImages)
                            .otherColourVariation(Collections.emptyList())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<ProductItemPageRequest> getAllProductItems(Integer items, String sortBy, String sortOrder) {

        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(sortOrder)) {
            direction = Sort.Direction.DESC;
        }

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(0, items, sort);
        List<ProductItem> productItems = productItemRepository.findAll(pageable).getContent();

        return productItems.stream().map(productItem -> {
                    ProductItemPageRequest response = new ProductItemPageRequest();
                    response.setId(productItem.getId());
                    response.setPrice(productItem.getPrice());
                    response.setDiscount(productItem.getDiscount());
                    response.setProductCode(productItem.getProductCode());
                    response.setQtyInStock(productItem.getQtyInStock());
                    response.setProductImages(productItem.getProductImages().stream()
                            .map(ProductImage::getImageFilename)
                            .collect(Collectors.toList()));
                    response.setVariations(productItem.getVariationOptions().stream()
                            .collect(Collectors.groupingBy(option -> option.getVariation().getId()))
                            .entrySet().stream()
                            .map(entry -> {
                                Variation variation = entry.getValue().get(0).getVariation();
                                VariationResponse variationResponse = new VariationResponse();
                                variationResponse.setId(variation.getId());
                                variationResponse.setCategoryId(variation.getCategory() != null ? variation.getCategory().getId() : null);
                                variationResponse.setName(variation.getName());

                                variationResponse.setOptions(entry.getValue().stream()
                                        .map(option -> {
                                            VariationOptionResponse optionResponse = new VariationOptionResponse();
                                            optionResponse.setId(option.getId());
                                            optionResponse.setValue(option.getValue());
                                            return optionResponse;
                                        })
                                        .collect(Collectors.toList()));
                                return variationResponse;
                            }).collect(Collectors.toList()));
                    response.setProductName(productItem.getProduct().getProductName());
                    response.setProductDescription(productItem.getProduct().getDescription());
                    response.setCategoryId(productItem.getProduct().getCategory().getId());

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProductItem(Integer productItemId) {
        ProductItem productItem = productItemRepository.findById(productItemId)
                .orElseThrow(() -> new RuntimeException("Product item with this ID does not exist!"));

        if (productItem.getProduct() != null) {
            productItem.getProduct().getProductItems().remove(productItem);
        }

        if (productItem.getVariationOptions() != null) {
            for (VariationOption variationOption : productItem.getVariationOptions()) {
                variationOption.getProductItems().remove(productItem);
            }

            productItemRepository.delete(productItem);
        }
    }
}
