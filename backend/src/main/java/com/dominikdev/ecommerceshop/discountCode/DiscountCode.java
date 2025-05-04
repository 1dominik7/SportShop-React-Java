package com.dominikdev.ecommerceshop.discountCode;

import com.dominikdev.ecommerceshop.shopOrder.ShopOrder;
import com.dominikdev.ecommerceshop.shoppingCart.ShoppingCart;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "discount_code")
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String code;
    private LocalDateTime expiryDate;
    private Integer discount;

    @ManyToMany(mappedBy = "discountCodes")
    @JsonBackReference
    private Set<ShoppingCart> shoppingCarts = new HashSet<>();
}
