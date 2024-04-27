package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import com.ayushjainttn.bootcampproject.ecommerce.dto.category.CategoryViewDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.category.ParentCategoryViewDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.category.ProductCategoryViewDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CustomerProductViewDto {
    private Long productId;
    private String productName;
    private ProductCategoryViewDto category;
    private String productBrand;
    private String productDescription;
    private Boolean productIsCancellable;
    private Boolean productIsReturnable;
    private Boolean productIsActive;
    private Set<CustomerProductVariationViewDto> productVariationSet;
}
