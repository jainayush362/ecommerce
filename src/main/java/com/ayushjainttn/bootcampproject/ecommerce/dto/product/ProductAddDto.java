package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class ProductAddDto {
    @NotBlank(message = "{productName.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_PRODUCT_NAME)
    private String productName;
    @NotNull(message = "{categoryId.notNull}")
    @Positive
    private Long categoryId;
    @NotBlank(message = "{productBrand.notBlank}")
    private String productBrand;
    //optional parameters
    @Pattern(regexp = GlobalExpressions.RGX_PRODUCT_DESCRIPTION)
    private String productDescription;
    private Boolean isCancellable;
    private Boolean isReturnable;
}
