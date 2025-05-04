package com.dominikdev.ecommerceshop.discountCode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Integer> {

    List<DiscountCode> findByExpiryDateGreaterThanEqual(LocalDateTime now);

    Optional<DiscountCode> findByCode(String code);
}
