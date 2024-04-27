package com.ayushjainttn.bootcampproject.ecommerce.repository;

import com.ayushjainttn.bootcampproject.ecommerce.entity.CategoryMetadataField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMetadataFieldRepository extends JpaRepository<CategoryMetadataField, Long> {
//    @Query(value = "SELECT * FROM category_metadata_field_table", nativeQuery = true)
//    public Page findAllCategoryMetadataFields(Pageable pageable);

    public CategoryMetadataField findByCategoryMetadataFieldNameIgnoreCase(String name);
}
