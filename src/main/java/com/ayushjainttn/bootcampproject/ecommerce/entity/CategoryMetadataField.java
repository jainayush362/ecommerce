package com.ayushjainttn.bootcampproject.ecommerce.entity;

import com.ayushjainttn.bootcampproject.ecommerce.utils.Auditable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category_metadata_field_table")
public class CategoryMetadataField extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_metadata_field_id", nullable = false, updatable = false)
    private Long categoryMetadataFieldId;

    @NaturalId
    @Column(name = "category_metadata_field_name", nullable = false, unique = true, updatable = false)
    private String categoryMetadataFieldName;

    @OneToMany(mappedBy = "categoryMetadataField", fetch = FetchType.EAGER)
    private List<CategoryMetadataFieldValue> categoryMetadataFieldValues = new ArrayList<>();

    public void addCategoryMetadataFieldValues(CategoryMetadataFieldValue categoryMetadataFieldValue) {
        categoryMetadataFieldValues.add(categoryMetadataFieldValue);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryMetadataField categoryMetadataField = (CategoryMetadataField) o;
        return Objects.equals(categoryMetadataFieldName, categoryMetadataField.categoryMetadataFieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryMetadataFieldName);
    }
}