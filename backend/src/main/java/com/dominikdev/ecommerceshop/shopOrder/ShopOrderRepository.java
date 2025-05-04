package com.dominikdev.ecommerceshop.shopOrder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Integer> {
    List<ShopOrder> findByUserId(Integer userId);

    ShopOrder findByPaymentPaymentIntentId(String paymentIntentId);
}
