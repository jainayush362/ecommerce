package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import com.ayushjainttn.bootcampproject.ecommerce.entity.Product;
import com.ayushjainttn.bootcampproject.ecommerce.payload.ProductImage;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
public class ProductVariationViewDto {
    private Long productVariationId;
    private ProductImage productVariationImage;
    private Integer productVariationQuantity;
    private Double productVariationPrice;
    private Boolean productVariationIsActive;
    private Object productVariationMetadata;
    private ParentProductViewDto product;
}
