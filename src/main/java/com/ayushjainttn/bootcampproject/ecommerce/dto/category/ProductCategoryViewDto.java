package com.ayushjainttn.bootcampproject.ecommerce.dto.category;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
@Getter
@Setter
public class ProductCategoryViewDto {
    private Long categoryId;
    private String categoryName;
    private List<CategoryMetadataFieldValueViewDto> categoryMetadataFieldValues;
}
