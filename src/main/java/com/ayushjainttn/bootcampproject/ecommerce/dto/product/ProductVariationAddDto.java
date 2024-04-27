package com.ayushjainttn.bootcampproject.ecommerce.dto.product;

import com.ayushjainttn.bootcampproject.ecommerce.entity.Product;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Map;

@Getter
@Setter
//@TypeDef(name = "json", typeClass = JsonStringType.class)
public class ProductVariationAddDto {
    @NotNull(message = "{productId.notNull}")
    @Positive
    private Long productId;
    @NotNull(message = "{productVariationQuantity.notNull}")
    private Integer productVariationQuantity;
    @NotNull(message = "{productVariationPrice.notNull}")
    private Double productVariationPrice;
    @NotNull(message = "{productVariationMetadata.notNull}")
    private Map<String,String> productVariationMetadata;
}

/*

{
"productId":,
"productVariationQuantity":,
"productVariationPrice":,
"productVariationMetadata":{

}
}

 */