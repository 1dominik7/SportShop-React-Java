package com.dominikdev.ecommerceshop.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("SELECT a FROM Address a JOIN a.users u WHERE u.id = :userId")
    List<Address> findAddressesByUserId(Integer userId);
}
