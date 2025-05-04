package com.dominikdev.ecommerceshop.shoppingCart;

import com.dominikdev.ecommerceshop.discountCode.DiscountCode;
import com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem.ShoppingCartItem;
import com.dominikdev.ecommerceshop.user.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shopping_cart")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ShoppingCartItem> shoppingCartItems = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "shopping_cart_discount",
            joinColumns = @JoinColumn(name = "shopping_cart_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_code_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"shopping_cart_id", "discount_code_id"})
    )
    @JsonManagedReference
    private Set<DiscountCode> discountCodes = new HashSet<>();
}
