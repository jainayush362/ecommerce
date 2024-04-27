package com.ayushjainttn.bootcampproject.ecommerce.entity;

import com.ayushjainttn.bootcampproject.ecommerce.utils.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category_table")
public class Category extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id", nullable = false, updatable = false)
    private Long categoryId;

    //@NaturalId
    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategoryId;

    @OneToMany(mappedBy = "parentCategoryId", fetch = FetchType.EAGER)
    private Set<Category> linkedCategory = new HashSet<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<CategoryMetadataFieldValue> categoryMetadataFieldValues = new ArrayList<>();

    public void addLinkedCategories(Category category) {
        if(category != null) {
            if(linkedCategory == null) linkedCategory = new HashSet<>();
            category.setParentCategoryId(this);
            linkedCategory.add(category);
        }
    }

    public void addCategoryMetadataFieldValues(CategoryMetadataFieldValue categoryMetadataFieldValue) {
        categoryMetadataFieldValues.add(categoryMetadataFieldValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryName, category.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryName);
    }

    public boolean isLeafCategory() {
        return linkedCategory.isEmpty();
    }

    public boolean isRootCategory() {
        return parentCategoryId == null;
    }
}