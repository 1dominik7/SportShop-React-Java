package com.dominikdev.ecommerceshop.shoppingCart;

import com.dominikdev.ecommerceshop.discountCode.DiscountCode;
import com.dominikdev.ecommerceshop.discountCode.DiscountCodeRepository;
import com.dominikdev.ecommerceshop.exceptions.APIException;
import com.dominikdev.ecommerceshop.product.productImage.ProductImage;
import com.dominikdev.ecommerceshop.product.productImage.ProductImageResponse;
import com.dominikdev.ecommerceshop.product.productItem.ProductItem;
import com.dominikdev.ecommerceshop.product.productItem.ProductItemRepository;
import com.dominikdev.ecommerceshop.product.productItem.response.ProductItemResponse;
import com.dominikdev.ecommerceshop.product.productItem.request.ProductItemRequest;
import com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem.ShoppingCarItemGetProdItemResponse;
import com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem.ShoppingCartItem;
import com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem.ShoppingCartItemRepository;
import com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem.ShoppingCartItemResponse;
import com.dominikdev.ecommerceshop.utils.AuthUtils;
import com.dominikdev.ecommerceshop.variation.Variation;
import com.dominikdev.ecommerceshop.variation.VariationResponse;
import com.dominikdev.ecommerceshop.variation.variationOption.VariationOption;
import com.dominikdev.ecommerceshop.variation.variationOption.VariationOptionResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ProductItemRepository productItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final DiscountCodeRepository discountCodeRepository;
    private final AuthUtils authUtils;
    private final ModelMapper modelMapper;

    public ShoppingCartResponse addProductToCart(Integer productItemId, Integer quantity) {

        ShoppingCart cart = createCart();

        ProductItem productItem = productItemRepository.findById(productItemId).orElseThrow(
                () -> new RuntimeException("Product with this id not found"));
        ShoppingCartItem shoppingCartItem = shoppingCartItemRepository.findCartItemByProductIdAndCartId(cart.getId(), productItemId);

        if (shoppingCartItem != null) {
            if (productItem.getQtyInStock() < shoppingCartItem.getQty() + quantity) {
                throw new APIException("You cannot add more than " + productItem.getQtyInStock() + " of " +
                        productItem.getProduct().getProductName() + " to the cart.");
            }

            shoppingCartItem.setQty(shoppingCartItem.getQty() + quantity);
            shoppingCartItemRepository.save(shoppingCartItem);
        } else {
            if (productItem.getQtyInStock() < quantity) {
                throw new APIException("Please, make an order of " + productItem.getProduct().getProductName() + "/" +
                        productItem.getProductCode() + " less than or equal to the quantity " + productItem.getQtyInStock() + ".");
            }

            ShoppingCartItem newShoppingCartItem = new ShoppingCartItem();
            newShoppingCartItem.setProductItem(productItem);
            newShoppingCartItem.setShoppingCart(cart);
            newShoppingCartItem.setQty(quantity);

            cart.getShoppingCartItems().add(newShoppingCartItem);

            shoppingCartItemRepository.save(newShoppingCartItem);
        }

        shoppingCartRepository.save(cart);


        ShoppingCartResponse shoppingCartResponse = modelMapper.map(cart, ShoppingCartResponse.class);

        List<ShoppingCartItemResponse> shoppingCartItemResponses = cart.getShoppingCartItems().stream().map(cartItem -> {
            ProductItemResponse productItemResponse = modelMapper.map(cartItem.getProductItem(), ProductItemResponse.class);

            List<Integer> variationOptionIds = cartItem.getProductItem().getVariationOptions().stream()
                    .map(variationOption -> variationOption.getId())
                    .collect(Collectors.toList());
            productItemResponse.setVariationOptionIds(variationOptionIds);

            productItemResponse.setQtyInStock(cartItem.getProductItem().getQtyInStock());

            return ShoppingCartItemResponse.builder()
                    .id(cartItem.getId())
                    .productItem(productItemResponse)
                    .qty(cartItem.getQty())
                    .build();
        }).collect(Collectors.toList());

        shoppingCartResponse.setShoppingCartItems(shoppingCartItemResponses);

        return shoppingCartResponse;

    }

    public ShoppingCartGetProdItemResponse getCart(String email, Integer cartId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findCartByEmailAndCartId(email, cartId);

        if (shoppingCart == null) {
            ShoppingCartGetProdItemResponse emptyResponse = new ShoppingCartGetProdItemResponse();
            emptyResponse.setId(cartId);
            emptyResponse.setShoppingCartItems(new ArrayList<>());
            emptyResponse.setDiscountCodes(new HashSet<>());
            return emptyResponse;
        }

        removeExpiredDiscountCodes(shoppingCart);

        ShoppingCartGetProdItemResponse shoppingCartResponse = new ShoppingCartGetProdItemResponse();
        shoppingCartResponse.setId(shoppingCart.getId());
        shoppingCartResponse.setDiscountCodes(shoppingCart.getDiscountCodes());

        List<ShoppingCarItemGetProdItemResponse> shoppingCartItemResponses = shoppingCart.getShoppingCartItems().stream()
                .map(cartItem -> {

                    ProductItemRequest productItemRequest = mapToProductItemRequestGrouped(cartItem.getProductItem());

                    return ShoppingCarItemGetProdItemResponse.builder()
                            .id(cartItem.getId())
                            .productItem(productItemRequest)
                            .qty(cartItem.getQty())
                            .productName(cartItem.getProductItem().getProduct().getProductName())
                            .build();
                })
                .collect(Collectors.toList());

        shoppingCartResponse.setShoppingCartItems(shoppingCartItemResponses);

        return shoppingCartResponse;
    }

    private void removeExpiredDiscountCodes(ShoppingCart shoppingCart) {
        LocalDateTime now = LocalDateTime.now();

        shoppingCart.getDiscountCodes().removeIf(discountCode ->
                discountCode.getExpiryDate().isBefore(now)
        );
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

        List<ProductImage> sortedImages = productItem.getProductImages().stream()
                .sorted(Comparator.comparingLong(ProductImage::getId))
                .collect(Collectors.toList());

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
                .productImages(sortedImages)
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

    public List<ShoppingCartResponse> getAllCarts() {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();

        if (shoppingCarts.isEmpty()) {
            return new ArrayList<>();
        }

        return shoppingCarts.stream().map(shoppingCart -> {
            ShoppingCartResponse shoppingCartResponse = modelMapper.map(shoppingCart, ShoppingCartResponse.class);

            List<ShoppingCartItemResponse> shoppingCartItemResponses = shoppingCart.getShoppingCartItems().stream()
                    .map(shoppingCartItem -> {
                        ProductItemResponse productItemResponse = modelMapper.map(shoppingCartItem.getProductItem(), ProductItemResponse.class);

                        List<Integer> variationOptionIds = shoppingCartItem.getProductItem().getVariationOptions().stream()
                                .map(variationOption -> variationOption.getId()).collect(Collectors.toList());
                        productItemResponse.setVariationOptionIds(variationOptionIds);

                        Set<ProductImageResponse> productImages = shoppingCartItem.getProductItem().getProductImages().stream()
                                .map(image -> new ProductImageResponse(image.getId(), image.getImageFilename()))
                                .collect(Collectors.toSet());
                        productItemResponse.setProductImages(productImages);

                        return ShoppingCartItemResponse.builder()
                                .id(shoppingCartItem.getId())
                                .productItem(productItemResponse)
                                .qty(shoppingCartItem.getQty())
                                .build();
                    }).collect(Collectors.toList());


            shoppingCartResponse.setShoppingCartItems(shoppingCartItemResponses);
            return shoppingCartResponse;
        }).collect(Collectors.toList());
    }

    @Transactional
    public ShoppingCartResponse updateProductQuantityInCart(Integer productItemId, Integer quantity) {
        String email = authUtils.loggedInEmail();
        ShoppingCart userCart = shoppingCartRepository.findCartByEmail(email);
        Integer shoppingCartId = userCart.getId();

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow(() -> new RuntimeException("Cart not found"));

        ProductItem productItem = productItemRepository.findById(productItemId).orElseThrow(() -> new RuntimeException("Product not found"));

        if (productItem.getQtyInStock() < quantity) {
            throw new APIException("Please, make an order of the " + productItem.getProduct().getProductName() + "/" + productItem.getProductCode()
                    + " less than or equal to the quantity " + productItem.getQtyInStock() + ".");
        }

        ShoppingCartItem shoppingCartItem = shoppingCartItemRepository.findCartItemByProductIdAndCartId(shoppingCartId, productItemId);

        if (shoppingCartItem == null) {
            throw new RuntimeException("Product " + productItem.getProduct().getProductName() + "/" + productItem.getProductCode() + " not available in the cart!");
        }

        int newQuantity = shoppingCartItem.getQty() + quantity;

        if (newQuantity > productItem.getQtyInStock()) {
            throw new APIException("The requested quantity is not available");
        }

        if (newQuantity < 0) {
            throw new RuntimeException("The resulting quantity cannot be negative.");
        }

        if (newQuantity == 0) {
            deleteProductFromCart(shoppingCartId, productItemId);
        } else {
            shoppingCartItem.setQty(shoppingCartItem.getQty() + quantity);
            shoppingCartRepository.save(shoppingCart);
        }

        ShoppingCartItem updatedItem = shoppingCartItemRepository.save(shoppingCartItem);
        if (updatedItem.getQty() == 0) {
            shoppingCartItemRepository.deleteById(updatedItem.getId());
        }

        ShoppingCartResponse shoppingCartResponse = modelMapper.map(shoppingCart, ShoppingCartResponse.class);

        List<ShoppingCartItemResponse> shoppingCartItemResponses = shoppingCart.getShoppingCartItems().stream().map(item -> {
            ProductItemResponse productItemResponse = modelMapper.map(item.getProductItem(), ProductItemResponse.class);

            List<Integer> variationOptionsIds = item.getProductItem().getVariationOptions().stream().map(variationOption -> variationOption.getId()).collect(Collectors.toList());
            productItemResponse.setVariationOptionIds(variationOptionsIds);

            productItemResponse.setQtyInStock(item.getProductItem().getQtyInStock());

            return new ShoppingCartItemResponse(
                    item.getId(),
                    productItemResponse,
                    item.getQty(),
                    item.getProductItem().getProduct().getProductName()
            );
        }).collect(Collectors.toList());

        shoppingCartResponse.setShoppingCartItems(shoppingCartItemResponses);
        return shoppingCartResponse;
    }

    private ShoppingCart createCart() {
        ShoppingCart userCart = shoppingCartRepository.findCartByEmail(authUtils.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(authUtils.loggedInUser());
        ShoppingCart newCart = shoppingCartRepository.save(shoppingCart);

        return newCart;
    }

    @Transactional
    public String deleteProductFromCart(Integer shoppingCartId, Integer productItemId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow(() -> new RuntimeException("Cart not found"));

        ShoppingCartItem shoppingCartItem = shoppingCartItemRepository.findCartItemByProductIdAndCartId(shoppingCartId, productItemId);

        if (shoppingCartItem == null) {
            throw new RuntimeException("Product not found");
        }

        shoppingCartItemRepository.deleteCartItemByProductIdAndCartId(shoppingCartId, productItemId);
        return "Product " + shoppingCartItem.getProductItem().getProduct().getProductName() + "/" + shoppingCartItem.getProductItem().getProductCode() + " removed from the cart!!";
    }

    public void addDiscountToCart(Integer shoppingCartId, String discountCode) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow(() -> new RuntimeException("Cart not found"));

        DiscountCode discount = discountCodeRepository.findByCode(discountCode).orElseThrow(() -> new RuntimeException("Discount with this code does not exist!"));

        if (discount.getExpiryDate() != null && discount.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Discount code has expired!");
        }

        if (!shoppingCart.getDiscountCodes().contains(discount)) {
            shoppingCart.getDiscountCodes().add(discount);
            shoppingCartRepository.save(shoppingCart);
        } else {
            throw new RuntimeException("Discount code already applied to this cart");
        }
    }

    @Transactional
    public String deleteCartById(Integer shoppingCartId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId).orElseThrow(() -> new RuntimeException("Cart Item with this id does not exist!"));

        shoppingCart.getShoppingCartItems().clear();

        shoppingCart.getDiscountCodes().clear();

        shoppingCartRepository.save(shoppingCart);

        shoppingCartRepository.delete(shoppingCart);

        return "CartItem " + shoppingCart.getId() + " removed";
    }
}
