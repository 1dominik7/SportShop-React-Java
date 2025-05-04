package com.dominikdev.ecommerceshop.product.productItem;

import com.dominikdev.ecommerceshop.orderLine.OrderLine;
import com.dominikdev.ecommerceshop.product.Product;
import com.dominikdev.ecommerceshop.product.productImage.ProductImage;
import com.dominikdev.ecommerceshop.shoppingCart.shoppingCartItem.ShoppingCartItem;
import com.dominikdev.ecommerceshop.variation.variationOption.VariationOption;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_item")
@EntityListeners(AuditingEntityListener.class)
public class ProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double price;
    private Integer discount;
    private String productCode;
    private Integer qtyInStock;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "products_id")
    @JsonBackReference
    private Product product;

    @ManyToMany(cascade = {CascadeType.ALL, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_item_images",
            joinColumns = @JoinColumn(name = "product_item_id"),
            inverseJoinColumns = @JoinColumn(name="product_image_id"))
    @JsonManagedReference
    private List<ProductImage> productImages = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.ALL, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_configuration",
            joinColumns = @JoinColumn(name = "product_item_id"),
            inverseJoinColumns = @JoinColumn(name = "variation_option_id")
    )
    @JsonManagedReference
    private List<VariationOption> variationOptions = new ArrayList<>();

    @OneToMany(mappedBy = "productItem", cascade = {CascadeType.ALL, CascadeType.REMOVE})
    @JsonManagedReference
    private List<ShoppingCartItem> shoppingCartItems = new ArrayList<>();

    @OneToMany(mappedBy = "productItem", cascade = {CascadeType.ALL})
    @JsonManagedReference
    private List<OrderLine> orderLines = new ArrayList<>();
}
