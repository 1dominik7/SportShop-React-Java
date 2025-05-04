package com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Integer> {

    @Query("SELECT ci FROM ShoppingCartItem ci WHERE ci.shoppingCart.id = ?1 AND ci.productItem.id = ?2")
    ShoppingCartItem findCartItemByProductIdAndCartId(Integer cartId, Integer productId);

    @Modifying
    @Query("DELETE FROM ShoppingCartItem ci WHERE ci.shoppingCart.id = ?1 AND ci.productItem.id = ?2")
    void deleteCartItemByProductIdAndCartId(Integer cartId, Integer productId);

}
