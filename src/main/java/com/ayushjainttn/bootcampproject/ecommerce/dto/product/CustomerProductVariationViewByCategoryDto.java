package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import com.ayushjainttn.bootcampproject.ecommerce.payload.ProductImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerProductVariationViewByCategoryDto {
    private Long productVariationId;
    private ProductImage productVariationImage;
}
