package com.dominikdev.ecommerceshop.paymentType;

import com.dominikdev.ecommerceshop.userPaymentMethod.UserPaymentMethod;
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
@Table(name = "payment_type")
public class PaymentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String value;

    @OneToMany(mappedBy = "paymentType",cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<UserPaymentMethod> userPaymentMethodList = new ArrayList<>();
}
