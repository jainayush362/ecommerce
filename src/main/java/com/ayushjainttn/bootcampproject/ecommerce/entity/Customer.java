package com.ayushjainttn.bootcampproject.ecommerce.entity;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Where(clause = "is_deleted = false")
@Table(name = "customer_table")
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends User{

    @Column(name = "customer_contact", nullable = false, length = 10)
    private String customerContact;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean customerIsDeleted = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Address> addressSet = new HashSet<>();

    public void addAddress(Address address) {
        if(address != null) {
            if(addressSet == null) addressSet = new HashSet<>();
            if(!addressSet.contains(address)) {
                address.setUser(this);
                addressSet.add(address);
            }
        }
    }

    public void deleteAddress(Address address) {
        if(address != null) {
            if(addressSet == null) return;
            if(addressSet.contains(address)) {
                addressSet.remove(address);
                address.setUser(null);
                address.setIsDeleted(true);
            }
        }
    }

}
