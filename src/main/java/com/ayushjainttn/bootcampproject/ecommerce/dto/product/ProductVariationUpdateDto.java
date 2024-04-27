package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Map;

@Getter
@Setter
public class ProductVariationUpdateDto {

    private Integer productVariationQuantity;

    private Double productVariationPrice;

    private Map<String,String> productVariationMetadata;

    private Boolean productVariationIsActive;
}
