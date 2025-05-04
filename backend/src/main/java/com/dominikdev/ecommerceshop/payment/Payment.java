package com.dominikdev.ecommerceshop.payment;

import com.dominikdev.ecommerceshop.shopOrder.ShopOrder;
import com.dominikdev.ecommerceshop.userPaymentMethod.UserPaymentMethod;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false, unique = true)
    private String paymentIntentId;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true)
    @JsonBackReference
    private ShopOrder shopOrder;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private UserPaymentMethod paymentMethod;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;


    public enum PaymentStatus {
        REQUIRES_PAYMENT_METHOD,
        REQUIRES_CONFIRMATION,
        REQUIRES_ACTION,
        PROCESSING,
        SUCCEEDED,
        CANCELED,
        FAILED
    }

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
    }

    @PreUpdate
    protected  void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
