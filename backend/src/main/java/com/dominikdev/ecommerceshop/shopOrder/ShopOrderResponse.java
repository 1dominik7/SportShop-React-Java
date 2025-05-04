package com.dominikdev.ecommerceshop.shopOrder;

import com.dominikdev.ecommerceshop.address.Address;

import com.dominikdev.ecommerceshop.orderLine.OrderLineResponse;
import com.dominikdev.ecommerceshop.orderStatus.OrderStatus;
import com.dominikdev.ecommerceshop.payment.PaymentResponse;
import com.dominikdev.ecommerceshop.shippingMethod.ShippingMethod;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopOrderResponse {

    private Integer id;
    private Integer userId;
    private LocalDateTime orderDate;
    private Address address;
    private ShippingMethod shippingMethod;
    private Double orderTotal;
    private Double finalOrderTotal;
    private OrderStatus orderStatus;
    private List<OrderLineResponse> orderLines;
    private PaymentResponse payment;
    private Integer appliedDiscountValue;
}
