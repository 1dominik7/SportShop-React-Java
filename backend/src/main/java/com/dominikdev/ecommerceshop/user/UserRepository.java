package com.dominikdev.ecommerceshop.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u JOIN u.addresses a WHERE u.id = :userId AND a.id = :addressId")
    boolean existsByUserIdAndAddressId(Integer userId, Integer addressId);
}
