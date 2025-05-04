package com.dominikdev.ecommerceshop.shopOrder;


import com.dominikdev.ecommerceshop.address.Address;
import com.dominikdev.ecommerceshop.address.AddressRepository;
import com.dominikdev.ecommerceshop.address.AddressRequest;
import com.dominikdev.ecommerceshop.orderLine.OrderLine;
import com.dominikdev.ecommerceshop.orderLine.OrderLineResponse;
import com.dominikdev.ecommerceshop.orderStatus.OrderStatus;
import com.dominikdev.ecommerceshop.orderStatus.OrderStatusRepository;
import com.dominikdev.ecommerceshop.payment.PaymentResponse;
import com.dominikdev.ecommerceshop.product.productImage.ProductImage;
import com.dominikdev.ecommerceshop.product.productItem.ProductItem;
import com.dominikdev.ecommerceshop.product.productItem.response.ProductItemToOrderResponse;
import com.dominikdev.ecommerceshop.shippingMethod.ShippingMethod;
import com.dominikdev.ecommerceshop.shippingMethod.ShippingMethodRepository;
import com.dominikdev.ecommerceshop.shoppingCart.ShoppingCart;
import com.dominikdev.ecommerceshop.shoppingCart.ShoppingCartRepository;
import com.dominikdev.ecommerceshop.user.User;
import com.dominikdev.ecommerceshop.user.UserRepository;
import com.dominikdev.ecommerceshop.variation.VariationShortResponse;
import com.dominikdev.ecommerceshop.variation.variationOption.VariationOptionWithVariationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopOrderService {

    private final ShopOrderRepository shopOrderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final AddressRepository addressRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;

    @Transactional
    public ShopOrder createShopOrder(ShopOrderRequest request) {

        ShoppingCart cart = shoppingCartRepository.findById(request.getCartId())
                .orElseThrow(() -> new RuntimeException("Cart not found!"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        ShippingMethod shippingMethod = shippingMethodRepository.findById(request.getShippingMethodId())
                .orElseThrow(() -> new RuntimeException("Shipping method not found!"));

        Address address = processAddress(request.getAddressRequest(), user);

        OrderStatus initialStatus = orderStatusRepository.findByStatus("awaiting payment")
                .orElseThrow(() -> new IllegalStateException("Initial order status not found"));

        ShopOrder order = ShopOrder.builder()
                .userId(user.getId())
                .orderDate(LocalDateTime.now())
                .address(address)
                .shippingMethod(shippingMethod)
                .orderTotal(request.getOrderTotal())
                .finalOrderTotal(request.getFinalOrderTotal())
                .appliedDiscountValue(request.getAppliedDiscountValue())
                .orderStatus(initialStatus)
                .build();

        List<OrderLine> orderLines = cart.getShoppingCartItems().stream()
                .map(item -> {
                            ProductItem productItem = item.getProductItem();
                            double basePrice = productItem.getPrice();
                            Integer discount = productItem.getDiscount();

                            double finalPrice = discount > 0 ? basePrice * (1 - ((double) discount / 100)) : basePrice;

                            return OrderLine.builder()
                                    .productItem(item.getProductItem())
                                    .productName(item.getProductItem().getProduct().getProductName())
                                    .qty(item.getQty())
                                    .price(finalPrice)
                                    .shopOrder(order)
                                    .build();
                        }
                )
                .collect(Collectors.toList());

        order.setOrderLines(orderLines);

        return shopOrderRepository.save(order);
    }

    private Address processAddress(AddressRequest addressRequest, User user) {

        if (addressRequest.getId() != null) {
            Address address = addressRepository.findById(addressRequest.getId()).orElseThrow(() -> new RuntimeException("Address not found!"));
            address.setId(address.getId());
            address.setCountry(addressRequest.getCountry());
            address.setCity(addressRequest.getCity());
            address.setFirstName(addressRequest.getFirstName());
            address.setLastName(addressRequest.getLastName());
            address.setPostalCode(addressRequest.getPostalCode());
            address.setStreet(addressRequest.getStreet());
            address.setPhoneNumber(addressRequest.getPhoneNumber());
            address.setAddressLine1(addressRequest.getAddressLine1());
            address.setAddressLine2(addressRequest.getAddressLine2());
            return addressRepository.save(address);

        } else {
            Address newAddress = Address.builder()
                    .country(addressRequest.getCountry())
                    .city(addressRequest.getCity())
                    .firstName(addressRequest.getFirstName())
                    .lastName(addressRequest.getLastName())
                    .postalCode(addressRequest.getPostalCode())
                    .street(addressRequest.getStreet())
                    .phoneNumber(addressRequest.getPhoneNumber())
                    .addressLine1(addressRequest.getAddressLine1())
                    .addressLine2(addressRequest.getAddressLine2())
                    .build();

            user.getAddresses().add(newAddress);
            userRepository.save(user);

            return addressRepository.save(newAddress);
        }
    }

    public List<ShopOrderResponse> getUserShopOrders(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with this id doesn't exists"));

        List<ShopOrder> shopOrders = shopOrderRepository.findByUserId(userId);

        return shopOrders.stream()
                .map(this::convertToShopOrderResponse).collect(Collectors.toList());
    }

    private ShopOrderResponse convertToShopOrderResponse(ShopOrder shopOrder) {
        return ShopOrderResponse.builder()
                .id(shopOrder.getId())
                .userId(shopOrder.getUserId())
                .orderDate(shopOrder.getOrderDate())
                .address(shopOrder.getAddress())
                .shippingMethod(shopOrder.getShippingMethod())
                .orderTotal(shopOrder.getOrderTotal())
                .finalOrderTotal(shopOrder.getFinalOrderTotal())
                .orderStatus(shopOrder.getOrderStatus())
                .orderLines(shopOrder.getOrderLines().stream()
                        .map(orderLine -> {
                            return OrderLineResponse.builder()
                                    .id(orderLine.getId())
                                    .productName(orderLine.getProductName())
                                    .productItem(mapProductItem(orderLine.getProductItem()))
                                    .qty(orderLine.getQty())
                                    .price(orderLine.getPrice())
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .payment(shopOrder.getPayment() != null ? PaymentResponse.builder()
                        .id(shopOrder.getPayment().getId())
                        .transactionId(shopOrder.getPayment().getTransactionId())
                        .shopOrderId(shopOrder.getId())
                        .paymentMethod(shopOrder.getPayment().getPaymentMethod())
                        .createdAt(shopOrder.getPayment().getCreatedAt())
                        .updatedAt(shopOrder.getPayment().getUpdatedAt())
                        .status(shopOrder.getPayment().getStatus()).build() : null)
                .appliedDiscountValue(shopOrder.getAppliedDiscountValue())
                .build();
    }

    public ProductItemToOrderResponse mapProductItem(ProductItem productItem) {
        return ProductItemToOrderResponse.builder()
                .id(productItem.getId())
                .price(productItem.getPrice())
                .discount(productItem.getDiscount())
                .productCode(productItem.getProductCode())
                .qtyInStock(productItem.getQtyInStock())
                .productId(productItem.getProduct().getId())
                .variationOptions(productItem.getVariationOptions().stream()
                        .map(variationOption -> VariationOptionWithVariationResponse.builder()
                                .id(variationOption.getId())
                                .value(variationOption.getValue())
                                .variation(new VariationShortResponse(
                                        variationOption.getVariation().getId(),
                                        variationOption.getVariation().getName()
                                ))
                                .build())
                        .collect(Collectors.toList()))
                .productImages(productItem.getProductImages().stream()
                        .sorted(Comparator.comparing(ProductImage::getId))
                        .map(image -> ProductImage.builder()
                                .id(image.getId())
                                .imageFilename(image.getImageFilename())
                                .build())
                        .collect(Collectors.toList()))
                .productName(productItem.getProduct().getProductName())
                .productDescription(productItem.getProduct().getDescription())
                .build();
    }


    public ShopOrderResponse getUserShopOrderById(Integer shopOrderId, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with this id doesn't exists"));
        ShopOrder shopOrder = shopOrderRepository.findById(shopOrderId).orElseThrow(() -> new RuntimeException("Shop order with this id doesn't exists"));

        if (!shopOrder.getUserId().equals(userId)) {
            throw new RuntimeException("This order does not belong to the user");
        }

        return convertToShopOrderResponse(shopOrder);
    }
}
