package com.ayushjainttn.bootcampproject.ecommerce.dto.category;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class CategoryMetadataFieldValuesAddDto {
    @NotNull(message = "{categoryId.notNull}")
    @Positive
    private Long categoryId;
    @NotNull(message = "{categoryMetadataFieldId.notNull}")
    @Positive
    private Long categoryMetadataFieldId;
    @NotBlank(message = "{categoryMetadataFieldValues.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_CATEGORY_METADATA_FIELD_VALUE)
    private String categoryMetadataFieldValues;
}
