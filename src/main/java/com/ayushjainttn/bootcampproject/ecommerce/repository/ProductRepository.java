package com.ayushjainttn.bootcampproject.ecommerce.repository;

import com.ayushjainttn.bootcampproject.ecommerce.entity.Category;
import com.ayushjainttn.bootcampproject.ecommerce.entity.Product;
import com.ayushjainttn.bootcampproject.ecommerce.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Product findByProductNameIgnoreCaseAndProductBrandIgnoreCaseAndCategoryAndSeller(String productName, String productBrand, Category category, Seller Seller);
    @Query(value = "SELECT * FROM products_table WHERE product_id = :productId AND is_deleted=0", nativeQuery = true)
    public Product findUsingProductId(@Param("productId") Long productId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE products_table SET is_deleted = 1 WHERE product_id = :productId", nativeQuery = true)
    public void deleteUsingProductId(@Param("productId") Long productId);

    @Query(value = "SELECT * FROM products_table WHERE is_deleted=0 AND seller_id = :sellerId", nativeQuery = true)
    public Page<Product> findAllProducts(Long sellerId, Pageable pageable);
    @Query(value = "SELECT * FROM products_table WHERE is_deleted=0 AND is_active=1 AND category_id = :categoryId", nativeQuery = true)
    public Page<Product> findAllProductsUsingCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query(value = "SELECT * FROM products_table WHERE product_id = :productId AND is_deleted=0 AND is_active=1", nativeQuery = true)
    public Product findActiveProductsUsingProductId(@Param("productId") Long productId);

    @Query(value = "SELECT * FROM products_table WHERE is_deleted=0 AND is_active=1 AND category_id IN (:categoryId)", nativeQuery = true)
    public Page<Product> findAllProductsUsingCategoryIdList(@Param("categoryId") List<Long> categoryId, Pageable pageable);

    @Query(value = "SELECT * FROM products_table WHERE is_deleted=0 AND is_active=1", nativeQuery = true)
    public Page<Product> findAllActiveProducts(Pageable pageable);

    @Query(value = "SELECT * FROM products_table WHERE is_deleted=0 AND is_active=1", nativeQuery = true)
    public List<Product> findActiveProducts();

    @Query(value = "SELECT * FROM products_table WHERE is_deleted=0 AND is_active=0", nativeQuery = true)
    public List<Product> findInactiveProducts();

    @Query(value = "SELECT DISTINCT product_brand, product_id FROM products_table WHERE category_id IN (:categoryIdList) AND is_deleted=0",nativeQuery = true)
    public List<Object[]> findProductBrandsOfCategory(@Param("categoryIdList") List<Long> categoryIdList);

    @Query(value = "SELECT MIN(product_variation_price), MAX(product_variation_price) FROM product_variation_table WHERE product_id IN (:productIdList)",nativeQuery = true)
    public List<Object[]> findMinMaxPrice(@Param("productIdList") List<Long> productIdList);
}
