package com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem;

import com.dominikdev.ecommerceshop.product.productItem.ProductItem;
import com.dominikdev.ecommerceshop.shoppingCart.ShoppingCart;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shopping_cart_item")
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference
    private ShoppingCart shoppingCart;

    @ManyToOne
    @JoinColumn(name = "product_item_id", nullable = false)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    private ProductItem productItem;

    private Integer qty;

}
