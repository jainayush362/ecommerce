package com.ayushjainttn.bootcampproject.ecommerce.entity;

import com.ayushjainttn.bootcampproject.ecommerce.payload.UserImage;
import com.ayushjainttn.bootcampproject.ecommerce.utils.Auditable;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@Where(clause = "is_deleted = false")
@Table(name = "user_table")
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;

    @Column(name = "user_email", nullable = false, unique = true, updatable = false)
    private String userEmail;

    @Column(name = "first_name", nullable = false)
    private String userFirstName;

    @Column(name = "middle_name")
    private String userMiddleName;

    @Column(name = "last_name", nullable = false)
    private String userLastName;

    @Column(name = "user_password", nullable = false)
    private String userPassword;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean userIsDeleted = false;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default false")
    private Boolean userIsActive = false;

    @Column(name = "is_expired", nullable = false, columnDefinition = "boolean default false")
    private Boolean userIsExpired = false;

    @Column(name = "is_locked", nullable = false, columnDefinition = "boolean default false")
    private Boolean userIsLocked = false;

    @Column(name = "invalid_attempt_count", nullable = false, columnDefinition = "integer default 0")
    private Integer userInvalidAttemptCount = 0;

    @Column(name = "password_update_date")
    private Date userPasswordUpdateDate;

    @Column(name = "lock_date")
    private Date userLockDate;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role_table",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "user_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "role_id"
            )
    )
    private Role role;

    @Transient
    private UserImage image;
    public void setRole(Role role) {
        this.role = role;
        role.getUser().add(this);
    }

}
