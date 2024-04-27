package com.ayushjainttn.bootcampproject.ecommerce.repository;

import com.ayushjainttn.bootcampproject.ecommerce.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {
    public Seller findBySellerGstNumber(String sellerGstNumber);
    public Seller findBySellerCompanyNameIgnoreCase(String sellerCompanyName);
    public Seller findByUserEmailIgnoreCase(String userEmail);
    public Page<Seller> findByUserEmailContainingIgnoreCase(String userEmail, Pageable pageable);
}
