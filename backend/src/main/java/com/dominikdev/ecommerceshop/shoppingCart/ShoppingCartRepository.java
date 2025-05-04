package com.dominikdev.ecommerceshop.shoppingCart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer > {

    @Query("SELECT c FROM ShoppingCart c WHERE c.user.email = ?1")
    ShoppingCart findCartByEmail(String email);

    @Query("SELECT c FROM ShoppingCart c WHERE c.user.email = ?1 AND c.id = ?2")
    ShoppingCart findCartByEmailAndCartId(String email, Integer shoppingCartId);
}
