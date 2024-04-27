package com.ayushjainttn.bootcampproject.ecommerce.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_table")
@EqualsAndHashCode
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id", updatable = false, nullable = false)
    private Long roleId;

    @NotNull
    @Column(name = "role_authority", nullable = false, unique = true)
    private String authority;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    private Set<User> user = new HashSet<>();
}
