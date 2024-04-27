package com.ayushjainttn.bootcampproject.ecommerce.dto.category;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class CategoryAddDto {
    @Positive
    private Long parentCategoryId;

    @NotBlank(message = "{categoryName.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_CATEGORY_NAME)
    private String categoryName;
}
