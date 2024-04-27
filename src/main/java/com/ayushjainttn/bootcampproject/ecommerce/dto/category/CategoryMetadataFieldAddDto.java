package com.ayushjainttn.bootcampproject.ecommerce.dto.category;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CategoryMetadataFieldAddDto {
    @NotBlank(message = "{categoryMetadataField.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_ALPHABETS)
    private String categoryMetadataFieldName;
}
