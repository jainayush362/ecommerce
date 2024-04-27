package com.ayushjainttn.bootcampproject.ecommerce.service;

import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryAddDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryMetadataFieldAddDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryMetadataFieldValuesAddDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface CategoryService {
    public void isCategoryUniqueInSubTree(String newCategoryName, Category parentCategory);
    public void isCategoryUniqueFromRootToLeaf(String newCategoryName, Category parentCategory);
    public void isCategoryUniqueInRoot(String newCategoryName);
    public ResponseEntity addCategory(CategoryAddDto categoryAddDto);
    public ResponseEntity updateCategory(int id, CategoryUpdateDto categoryUpdateDto);
    public ResponseEntity addCategoryMetadataField(CategoryMetadataFieldAddDto categoryMetadataFieldAddDto);
    public ResponseEntity addCategoryMetadataFieldValues(CategoryMetadataFieldValuesAddDto categoryMetadataFieldValuesAddDto);
    public ResponseEntity updateCategoryMetadataFieldValues(CategoryMetadataFieldValuesAddDto categoryMetadataFieldValuesAddDto);
    public Page getAllCategoryMetadataFields(int pageOffset, int pageSize, String sortProperty, String sortDirection);
    public Page getAllCategories(int pageOffset, int pageSize, String sortProperty, String sortDirection);
    public ResponseEntity getCategoryUsingId(int categoryId);
    public ResponseEntity getAllCategoriesForSeller();
    public ResponseEntity getCategoriesForCustomer();
    public ResponseEntity getCategoriesForCustomerUsingId(int categoryId);
    public ResponseEntity getFilterByCategoryId(int categoryId);
}
