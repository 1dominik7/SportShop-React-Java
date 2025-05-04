package com.dominikdev.ecommerceshop.userReview;

import com.dominikdev.ecommerceshop.orderLine.OrderLine;
import com.dominikdev.ecommerceshop.orderLine.OrderLineRepository;
import com.dominikdev.ecommerceshop.product.productItem.ProductItem;
import com.dominikdev.ecommerceshop.shopOrder.ShopOrder;
import com.dominikdev.ecommerceshop.shopOrder.ShopOrderRepository;
import com.dominikdev.ecommerceshop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserReviewService {

    private final UserReviewRepository userReviewRepository;
    private final UserRepository userRepository;
    private final OrderLineRepository orderLineRepository;
    private final ShopOrderRepository shopOrderRepository;

    public UserReview createReview(UserReviewRequest userReviewRequest, Integer userId) {

        OrderLine orderLine = orderLineRepository.findById(userReviewRequest.getOrderLineId())
                .orElseThrow(() -> new RuntimeException("Product not found in orders"));

        if (userReviewRepository.existsByUserIdAndOrderLine(userId, orderLine)) {
            throw new IllegalStateException("You already reviewed this product");
        }

        UserReview review = UserReview.builder()
                .user(userRepository.getReferenceById(userId))
                .orderLine(orderLine)
                .ratingValue(userReviewRequest.getRatingValue())
                .comment(userReviewRequest.getComment())
                .createdDate(LocalDateTime.now())
                .build();

        return userReviewRepository.save(review);
    }

    public List<ProductItem> getProductsForReview(Integer userId) {

        List<ShopOrder> orders = shopOrderRepository.findByUserId(userId);

        List<ProductItem> productItems = new ArrayList<>();
        for (ShopOrder order : orders) {
            for (OrderLine orderLine : order.getOrderLines()) {
                ProductItem productItem = orderLine.getProductItem();

                if (!userReviewRepository.existsByUserIdAndOrderLine(userId, orderLine)) {
                    productItems.add(productItem);
                }
            }
        }

        return productItems;
    }

    public List<ProductItemReviewResponse> getReviewsForProductItems(List<Integer> productItemIds) {

        List<UserReview> reviews = userReviewRepository.findByOrderLineProductItemIdInOrderByCreatedDateDesc(productItemIds);

        return productItemIds.stream().map(productItemId -> {
            List<UserReviewResponse> productReviews = reviews.stream().filter(review -> review.getOrderLine().getProductItem().getId().equals(productItemId))
                    .map(review -> new UserReviewResponse(
                            review.getId(),
                            review.getUser().getUsername(),
                            review.getRatingValue(),
                            review.getComment(),
                            review.getCreatedDate(),
                            review.getOrderLine().getId()
                    ))
                    .collect(Collectors.toList());

            return new ProductItemReviewResponse(
                    productItemId,
                    productReviews
            );
        }).collect(Collectors.toList());
    }

    public ProductReviewResponse getReviewForProduct(Integer productId) {
        List<UserReview> reviews = userReviewRepository
                .findByOrderLineProductItemProductId(productId);

        List<UserReviewResponse> productReviews = reviews.stream()
                .map(review -> new UserReviewResponse(
                        review.getId(),
                        review.getUser().getFullName(),
                        review.getRatingValue(),
                        review.getComment(),
                        review.getCreatedDate(),
                        review.getOrderLine().getId()
                ))
                .collect(Collectors.toList());

        int total = productReviews.size();
        double average = total == 0 ? 0.0 :
                productReviews.stream().mapToInt(UserReviewResponse::getRatingValue).average().orElse(0.0);

        return new ProductReviewResponse(
                productId,
                productReviews,
                average,
                total
        );
    }

    public List<ProductReviewSummaryResponse> getSummaryReviewForProduct(List<Integer> productIds) {
        List<UserReview> allReviews = userReviewRepository.findByOrderLineProductItemProductIdIn(productIds);

        return productIds.stream().map(productId -> {
            List<UserReview> productReviews = allReviews.stream().filter(review -> review.getOrderLine().getProductItem().getProduct().getId().equals(productId)).toList();

            double average = productReviews.isEmpty() ? 0.0 : productReviews.stream().mapToInt(UserReview::getRatingValue).average().orElse(0.0);

            return new ProductReviewSummaryResponse(
                    productId,
                    average,
                    productReviews.size()
            );
        }).toList();
    }

    public boolean canUserReviewProduct(Integer userId, Integer productItemId, Integer orderLineId) {
        boolean hasPurchased = orderLineRepository.existsByIdAndProductItemIdAndShopOrderUserId(orderLineId, productItemId, userId);
        boolean alreadyReviewed = userReviewRepository.existsByUserIdAndOrderLineId(userId, orderLineId);
        return hasPurchased && !alreadyReviewed;
    }


    public Map<Integer, Boolean> canUserReviewProducts(Integer userId, List<OrderLine> orderLines) {
        return orderLines.stream().collect(Collectors.toMap(
                ol -> ol.getId(),
                ol -> canUserReviewProduct(userId, ol.getProductItem().getId(), ol.getId())
        ));
    }

    public List<UserReviewResponse> getUserReviewByOrderLineId(List<Integer> orderLineIds) {
        List<UserReview> reviews = userReviewRepository.findByOrderLineIdIn(orderLineIds);

        return reviews.stream().map(review -> new UserReviewResponse(
                review.getId(),
                review.getUser().getFullName(),
                review.getRatingValue(),
                review.getComment(),
                review.getCreatedDate(),
                review.getOrderLine().getId()
        )).collect(Collectors.toList());
    }

    public UserReviewResponse editUserReview(Integer userReviewId, UserReviewRequest userReviewRequest, Integer userId) {
        UserReview userReview = userReviewRepository.findById(userReviewId).orElseThrow(() -> new RuntimeException("Review with this id does not exist!"));

        if (!userReview.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only edit your own reviews");
        }

        userReview.setRatingValue(userReviewRequest.getRatingValue());
        userReview.setComment(userReviewRequest.getComment());

        UserReview updatedReview = userReviewRepository.save(userReview);

        return UserReviewResponse.builder()
                .id(updatedReview.getId())
                .ratingValue(updatedReview.getRatingValue())
                .comment(updatedReview.getComment())
                .OrderLineId(updatedReview.getOrderLine().getId())
                .createdDate(updatedReview.getCreatedDate())
                .userName(updatedReview.getUser().getFullName()).build();
    }

    public void deleteUserReview(Integer userReviewId, Integer userId) {
        UserReview userReview = userReviewRepository.findById(userReviewId).orElseThrow(() -> new RuntimeException("Review with this id does not exist!"));

        if (!userReview.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only edit your own reviews");
        }

        userReviewRepository.delete(userReview);
    }
}
