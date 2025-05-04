package com.dominikdev.ecommerceshop.paymentType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Integer> {
    Optional<PaymentType> findByValue(String value);
}
