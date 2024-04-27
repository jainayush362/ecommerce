package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryViewDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.category.ProductCategoryViewDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProductViewDto {
    private Long productId;
    private String productName;
    private String productBrand;
    private String productDescription;
    private Boolean productIsCancellable;
    private Boolean productIsReturnable;
    private Boolean productIsActive;
    private ProductCategoryViewDto category;
}
