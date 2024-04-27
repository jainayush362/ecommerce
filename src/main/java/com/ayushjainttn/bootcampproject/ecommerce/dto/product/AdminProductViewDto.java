package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import com.ayushjainttn.bootcampproject.ecommerce.dto.category.ProductCategoryViewDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerProductViewDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class AdminProductViewDto {
    private Long productId;
    private String productName;
    private String productBrand;
    private String productDescription;
    private Boolean productIsCancellable;
    private Boolean productIsReturnable;
    private Boolean productIsActive;
    private SellerProductViewDto seller;
    private ProductCategoryViewDto category;
    private Set<AdminProductVariationViewDto> productVariationSet;
}
