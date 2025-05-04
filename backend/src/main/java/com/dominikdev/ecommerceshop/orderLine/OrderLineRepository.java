package com.dominikdev.ecommerceshop.orderLine;

import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderLineRepository extends JpaRepository<OrderLine, Integer> {

    boolean existsByIdAndProductItemIdAndShopOrderUserId(Integer orderLineId, Integer productItemId, Integer userId);
}
