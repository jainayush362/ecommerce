package com.ayushjainttn.bootcampproject.ecommerce.entity;

import com.ayushjainttn.bootcampproject.ecommerce.utils.Auditable;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products_table")
public class Product extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ProductVariation> productVariationSet = new HashSet<>();

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_brand", nullable = false)
    private String productBrand;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default false")
    private Boolean productIsActive = false;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean productIsDeleted = false;

    @Column(name = "is_cancellable", nullable = false, columnDefinition = "boolean default false")
    private Boolean productIsCancellable = false;

    @Column(name = "is_returnable", nullable = false, columnDefinition = "boolean default false")
    private Boolean productIsReturnable = false;

    public void addProductVariation(ProductVariation productVariation) {
        if(productVariation != null) {
            if(productVariationSet == null)
                productVariationSet = new HashSet<>();
                productVariation.setProduct(this);
                productVariationSet.add(productVariation);
            }
    }

    public void setSeller(Seller seller) {
        if (seller != null) {
            if (this.seller == seller)
                return;
            this.seller = seller;
        }
    }

    public void setCategory(Category category) {
        if (category != null) {
            if (category == this.category)
                return;
            this.category = category;
        }
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Product product = (Product) o;
//        return seller.getUserId().equals(product.seller.getUserId()) && category.getCategoryId().equals(product.category.getCategoryId()) && productName.equalsIgnoreCase(product.productName) && productBrand.equalsIgnoreCase(product.productBrand);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(seller.getUserId(), category.getCategoryId(), productName, productBrand);
//    }
}
