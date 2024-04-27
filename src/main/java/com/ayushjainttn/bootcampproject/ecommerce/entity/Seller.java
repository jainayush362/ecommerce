package com.ayushjainttn.bootcampproject.ecommerce.entity;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Where(clause = "is_deleted = false")
@Table(name = "seller_table")
@PrimaryKeyJoinColumn(name = "user_id")
public class Seller extends User{

    @Column(name = "gst_number", nullable = false, unique = true, length = 15)
    private String sellerGstNumber;

    @Column(name = "company_name", nullable = false, unique = true)
    private String sellerCompanyName;

    @Column(name = "company_contact", nullable = false, length = 10)
    private String sellerCompanyContact;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean sellerIsDeleted = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Address address;

    public void setAddress(Address address)
    {
        if(address != null && this.address == null) {
            this.address = address;
            address.setUser(this);
        }
    }
}
