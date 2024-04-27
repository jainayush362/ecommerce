package com.ayushjainttn.bootcampproject.ecommerce.dto.category;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CategoryViewDto {
    private Long categoryId;
    private String categoryName;
    private ParentCategoryViewDto parentCategoryId;
    private Set<LinkedCategoryViewDto> linkedCategory;
    private List<CategoryMetadataFieldValueViewDto> categoryMetadataFieldValues;
}
