package com.dominikdev.ecommerceshop.userReview;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductItemReviewResponse {

    private Integer productItemId;
    private List<UserReviewResponse> reviews;
}
