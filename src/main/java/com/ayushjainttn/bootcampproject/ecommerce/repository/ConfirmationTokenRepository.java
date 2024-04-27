package com.ayushjainttn.bootcampproject.ecommerce.repository;

import com.ayushjainttn.bootcampproject.ecommerce.entity.ConfirmationToken;
import com.ayushjainttn.bootcampproject.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    public ConfirmationToken findByUser(User user);
    public ConfirmationToken findByConfirmationToken(String confirmationToken);
}
