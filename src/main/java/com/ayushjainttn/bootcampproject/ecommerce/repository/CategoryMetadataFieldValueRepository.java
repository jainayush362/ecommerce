package com.ayushjainttn.bootcampproject.ecommerce.repository;

import com.ayushjainttn.bootcampproject.ecommerce.entity.CategoryMetadataFieldValue;
import com.ayushjainttn.bootcampproject.ecommerce.entity.CategoryMetadataFieldValueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMetadataFieldValueRepository extends JpaRepository<CategoryMetadataFieldValue, CategoryMetadataFieldValueId> {
}
