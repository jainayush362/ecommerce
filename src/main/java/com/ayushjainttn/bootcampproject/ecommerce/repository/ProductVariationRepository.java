package com.ayushjainttn.bootcampproject.ecommerce.repository;

import com.ayushjainttn.bootcampproject.ecommerce.entity.ProductVariation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE product_variation_table SET is_active=0 WHERE product_variation_id= :productVariationId", nativeQuery = true)
    public void updateIsActiveStatus(@Param("productVariationId") Long productVariationId);

    @Query(value = "SELECT * FROM product_variation_table WHERE product_id = :productId AND is_active=1", nativeQuery = true)
    public Page<ProductVariation> findAllUsingProductId(@Param("productId") Long productId, Pageable pageable);
}
