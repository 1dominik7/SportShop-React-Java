package com.dominikdev.ecommerceshop.userReview;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReviewResponse {
    private Integer id;
    private String userName;
    private Integer ratingValue;
    private String comment;
    private LocalDateTime createdDate;
    private Integer OrderLineId;
}
