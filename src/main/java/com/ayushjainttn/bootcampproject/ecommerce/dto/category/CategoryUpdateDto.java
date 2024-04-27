package com.ayushjainttn.bootcampproject.ecommerce.dto.category;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CategoryUpdateDto {
    @NotBlank(message = "{categoryName.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_CATEGORY_NAME)
    private String categoryName;
}
