package com.ayushjainttn.bootcampproject.ecommerce.entity;

import com.ayushjainttn.bootcampproject.ecommerce.utils.Auditable;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Where(clause = "is_deleted = false")
@Table(name = "address_table")
public class Address extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "address_id", updatable = false, nullable = false)
    private Long addressId;

    @Column(name = "address_city", nullable = false)
    private String city;

    @Column(name = "address_state", nullable = false)
    private String state;

    @Column(name = "address_country", nullable = false)
    private String country;

    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Column(name = "address_postal_code", nullable = false)
    private String postalCode;

    @Column(name = "address_label", nullable = false)
    private String label;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted = false;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                '}';
    }

}
