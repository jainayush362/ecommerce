package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import com.ayushjainttn.bootcampproject.ecommerce.payload.ProductImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerProductVariationViewDto {
    private Long productVariationId;
    private ProductImage productVariationImage;
    private Integer productVariationQuantity;
    private Double productVariationPrice;
    private Boolean productVariationIsActive;
    private Object productVariationMetadata;
}
