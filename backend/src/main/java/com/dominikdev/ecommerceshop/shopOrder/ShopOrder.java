package com.dominikdev.ecommerceshop.shopOrder;

import com.dominikdev.ecommerceshop.address.Address;
import com.dominikdev.ecommerceshop.orderLine.OrderLine;
import com.dominikdev.ecommerceshop.orderStatus.OrderStatus;
import com.dominikdev.ecommerceshop.payment.Payment;
import com.dominikdev.ecommerceshop.shippingMethod.ShippingMethod;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shop_order")
public class ShopOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "shipping_address_id", nullable = false)
    @JsonBackReference
    private Address address;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id", nullable = false)
    private ShippingMethod shippingMethod;

    private Double orderTotal;
    private Double finalOrderTotal;

    @ManyToOne
    @JoinColumn(name = "order_status_id")
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "shopOrder", cascade =CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderLine> orderLines = new ArrayList<>();

    @OneToOne(mappedBy = "shopOrder")
    @JsonManagedReference
    private Payment payment;

    private Integer appliedDiscountValue;
}
