package com.dominikdev.ecommerceshop.orderLine;

import com.dominikdev.ecommerceshop.product.productItem.ProductItem;
import com.dominikdev.ecommerceshop.shopOrder.ShopOrder;
import com.dominikdev.ecommerceshop.userReview.UserReview;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_line")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_item_id")
    @JsonBackReference
    private ProductItem productItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    @JsonBackReference
    private ShopOrder shopOrder;

    private Integer qty;
    private Double price;

    @OneToMany(mappedBy = "orderLine", cascade = {CascadeType.ALL})
    @JsonManagedReference
    private List<UserReview> userReviews = new ArrayList<>();
}
