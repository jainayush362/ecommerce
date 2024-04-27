package com.ayushjainttn.bootcampproject.ecommerce.repository;

import com.ayushjainttn.bootcampproject.ecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findByAuthority(String authority);
}
