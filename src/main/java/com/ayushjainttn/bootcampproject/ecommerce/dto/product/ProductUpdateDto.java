package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class ProductUpdateDto {

    @Pattern(regexp = GlobalExpressions.RGX_PRODUCT_NAME)
    private String productName;
    @Pattern(regexp = GlobalExpressions.RGX_PRODUCT_DESCRIPTION)
    private String productDescription;
    private Boolean isCancellable;
    private Boolean isReturnable;
}
