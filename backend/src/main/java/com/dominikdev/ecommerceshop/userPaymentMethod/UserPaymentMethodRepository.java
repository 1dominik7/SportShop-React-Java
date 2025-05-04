package com.dominikdev.ecommerceshop.userPaymentMethod;

import com.dominikdev.ecommerceshop.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPaymentMethodRepository extends JpaRepository<UserPaymentMethod, Integer> {

    List<UserPaymentMethod> findByUserId(Integer userId);

    Optional<UserPaymentMethod> findByUserIdAndLast4CardNumber(Integer userId, String last4CardNumber);
}
