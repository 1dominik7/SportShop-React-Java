package com.dominikdev.ecommerceshop.product.productItem;

import com.dominikdev.ecommerceshop.product.productItem.request.ProductItemRequest;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItemDTO {
        private List<ProductItemRequest> content;
        private Integer pageNumber;
        private Integer pageSize;
        private Long totalElements;
        private Integer totalPages;
        private boolean lastPage;
}
