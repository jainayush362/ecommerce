package com.ayushjainttn.bootcampproject.ecommerce.entity;

import com.ayushjainttn.bootcampproject.ecommerce.utils.Auditable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category_metadata_field_value_table")
public class CategoryMetadataFieldValue extends Auditable {
    @EmbeddedId
    @Column(name = "category_metadata_field_value_id")
    private CategoryMetadataFieldValueId categoryMetadataFieldValueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryMetadataFieldId")
    private CategoryMetadataField categoryMetadataField;

    @Column(name = "category_metadata_field_values", nullable = false)
    private String categoryMetadataFieldValues;

    public CategoryMetadataFieldValue(Category category, CategoryMetadataField categoryMetadataField, String categoryMetadataFieldValues) {
        this.category = category;
        this.categoryMetadataField = categoryMetadataField;
        this.categoryMetadataFieldValueId = new CategoryMetadataFieldValueId(category.getCategoryId(), categoryMetadataField.getCategoryMetadataFieldId());
        this.categoryMetadataFieldValues = categoryMetadataFieldValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CategoryMetadataFieldValue that = (CategoryMetadataFieldValue) o;
        return Objects.equals(category, that.category) &&
                Objects.equals(categoryMetadataField, that.categoryMetadataField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, categoryMetadataField);
    }
}