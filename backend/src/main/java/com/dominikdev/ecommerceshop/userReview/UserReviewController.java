package com.dominikdev.ecommerceshop.userReview;

import com.dominikdev.ecommerceshop.exceptions.UnauthorizedException;
import com.dominikdev.ecommerceshop.orderLine.OrderLine;
import com.dominikdev.ecommerceshop.orderLine.OrderLineService;
import com.dominikdev.ecommerceshop.product.productItem.ProductItem;
import com.dominikdev.ecommerceshop.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class UserReviewController {

    private final UserReviewService userReviewService;
    private final OrderLineService orderLineService;

    @PostMapping
    public ResponseEntity<UserReview> createReview(@RequestBody UserReviewRequest userReviewRequest, @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new UnauthorizedException("User is not authenticated");
        }

        UserReview userReview = userReviewService.createReview(userReviewRequest, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(userReview);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductItem>> getProductsForReview(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
        List<ProductItem> productItems = userReviewService.getProductsForReview(user.getId());
        return ResponseEntity.ok(productItems);
    }

    @GetMapping("/productItem")
    public ResponseEntity<List<ProductItemReviewResponse>> getProductItemReviews(@RequestParam List<Integer> productItemIds) {
        List<ProductItemReviewResponse> productItemReviewResponses = userReviewService.getReviewsForProductItems(productItemIds);
        return ResponseEntity.ok(productItemReviewResponses);
    }

    @GetMapping("/productById/{productId}")
    public ResponseEntity<ProductReviewResponse> getReviewForProduct(@PathVariable Integer productId){
        ProductReviewResponse productReviewResponse = userReviewService.getReviewForProduct(productId);
        return ResponseEntity.ok(productReviewResponse);
    }

    @GetMapping("/product-summary")
    public ResponseEntity<List<ProductReviewSummaryResponse>> getSummaryReviewForProduct(@RequestParam List<Integer> productIds){
        List<ProductReviewSummaryResponse> result = userReviewService.getSummaryReviewForProduct(productIds);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/products/can-review")
    public ResponseEntity<Map<Integer, Boolean>> canUserReviewProduct(@RequestParam List<Integer> orderLineIds, @AuthenticationPrincipal User user) {

        if (user == null) {
            Map<Integer, Boolean> canReviewMap = orderLineIds.stream()
                    .collect(Collectors.toMap(id -> id, id -> false));
            return ResponseEntity.ok(canReviewMap);
        }

        List<OrderLine> orderLines = orderLineService.getOrderLinesByIds(orderLineIds);

        Map<Integer, Boolean> canReviewMap = userReviewService.canUserReviewProducts(user.getId(), orderLines);

        return ResponseEntity.ok(canReviewMap);
    }

    @GetMapping("/byOrderLines")
    public ResponseEntity<List<UserReviewResponse>> getUserReviewByOrderLineId(@AuthenticationPrincipal User user, @RequestParam List<Integer> orderLineIds) {
        if (user == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
        List<UserReviewResponse> userReviewResponses = userReviewService.getUserReviewByOrderLineId(orderLineIds);
        return ResponseEntity.ok(userReviewResponses);
    }

    @PutMapping("/{userReviewId}")
    public ResponseEntity<UserReviewResponse> updateReview( @PathVariable Integer userReviewId,
                                                            @Valid @RequestBody UserReviewRequest userReviewRequest,
                                                            @AuthenticationPrincipal User user) {
        UserReviewResponse response = userReviewService.editUserReview(
                userReviewId,
                userReviewRequest,
                user.getId()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userReviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer userReviewId,
                             @AuthenticationPrincipal User user) {
        userReviewService.deleteUserReview(userReviewId, user.getId());
        return ResponseEntity.ok("User review has been successfully deleted!");
    }
}
