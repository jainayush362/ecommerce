package com.ayushjainttn.bootcampproject.ecommerce.dto.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentCategoryViewDto {
    private Long categoryId;
    private String categoryName;
    private ParentCategoryViewDto parentCategoryId;
}
