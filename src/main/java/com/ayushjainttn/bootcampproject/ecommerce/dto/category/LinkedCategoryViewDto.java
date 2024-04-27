package com.ayushjainttn.bootcampproject.ecommerce.dto.category;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class LinkedCategoryViewDto {
    private Long categoryId;
    private String categoryName;
    private Set<LinkedCategoryViewDto> linkedCategory;
}
