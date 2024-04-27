package com.ayushjainttn.bootcampproject.ecommerce.dto.category;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CategoryFilterViewDto {
    private List<CategoryMetadataFieldValueViewDto> metadata;
    private Set<String> brands;
    private Double minimumPrice;
    private Double maximumPrice;
}
