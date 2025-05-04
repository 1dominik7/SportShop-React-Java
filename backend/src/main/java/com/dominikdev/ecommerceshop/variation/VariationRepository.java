package com.dominikdev.ecommerceshop.variation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariationRepository extends JpaRepository<Variation, Integer> {

    List<Variation> findByCategoryId(Integer categoryId);
}
