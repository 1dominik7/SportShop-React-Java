package com.dominikdev.ecommerceshop.userPaymentMethod;

import com.dominikdev.ecommerceshop.payment.Payment;
import com.dominikdev.ecommerceshop.paymentType.PaymentType;
import com.dominikdev.ecommerceshop.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_payment_method")
public class UserPaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name="payment_type_id", nullable = false)
    @JsonBackReference
    private PaymentType paymentType;

    private String provider;
    private String last4CardNumber;
    private LocalDateTime paymentDate;
    private LocalDateTime expiryDate;
    private boolean isDefault;

    @OneToMany(mappedBy = "paymentMethod")
    @JsonBackReference
    private List<Payment> payments = new ArrayList<>();

}
