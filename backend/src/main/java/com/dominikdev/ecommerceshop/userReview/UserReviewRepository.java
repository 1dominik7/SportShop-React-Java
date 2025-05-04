package com.dominikdev.ecommerceshop.userReview;


import com.dominikdev.ecommerceshop.orderLine.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserReviewRepository extends JpaRepository<UserReview, Integer> {

    boolean existsByUserIdAndOrderLine(Integer userId, OrderLine orderLine);

    List<UserReview> findByOrderLineProductItemIdInOrderByCreatedDateDesc(List<Integer> productItemIds);

    List<UserReview> findByOrderLineProductItemProductId(Integer productId);

    List<UserReview> findByOrderLineProductItemProductIdIn(List<Integer> productIds);

    boolean existsByUserIdAndOrderLineId(Integer userId, Integer orderLineId);

    List<UserReview> findByOrderLineIdIn(List<Integer> orderLines);

}
