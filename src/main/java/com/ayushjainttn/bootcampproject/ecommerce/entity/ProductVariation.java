package com.ayushjainttn.bootcampproject.ecommerce.entity;

import com.ayushjainttn.bootcampproject.ecommerce.payload.ProductImage;
import com.ayushjainttn.bootcampproject.ecommerce.utils.Auditable;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_variation_table")
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class ProductVariation extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_variation_id")
    private Long productVariationId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_variation_quantity", nullable = false)
    private Integer productVariationQuantity;

    @Column(name = "product_variation_price", nullable = false)
    private Double productVariationPrice;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default true")
    private Boolean productVariationIsActive = true;

    @Type(type = "json")
    @Column(name = "metadata", columnDefinition = "json")
    private Object productVariationMetadata;

    public void setProduct(Product product){
        if(product != null) {
            if(this.product == product)
                return;
            else if(this.product == null) {
                this.product = product;
                product.addProductVariation(this);
            }

        }
    }

    @Transient
    private ProductImage image;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ProductVariation that = (ProductVariation) o;
//        return product.getProductId().equals(that.product.getProductId()) && productVariationQuantity.equals(that.productVariationQuantity) && productVariationPrice.equals(that.productVariationPrice) && productVariationMetadata.equals(that.productVariationMetadata);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(product.getProductId(), productVariationQuantity, productVariationPrice, productVariationMetadata);
//    }
}
