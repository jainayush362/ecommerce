package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryViewDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentProductViewDto {
    private Long productId;
    private String productName;
    private String productBrand;
    private String productDescription;
    private Boolean productIsCancellable;
    private Boolean productIsReturnable;
    private Boolean productIsActive;
}
