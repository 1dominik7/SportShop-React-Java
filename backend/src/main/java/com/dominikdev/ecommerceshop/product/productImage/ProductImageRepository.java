package com.dominikdev.ecommerceshop.product.productImage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long > {

        Optional<ProductImage> findByImageFilename(String imageFilename);

}
