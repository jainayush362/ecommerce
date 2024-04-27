package com.ayushjainttn.bootcampproject.ecommerce.repository;

import com.ayushjainttn.bootcampproject.ecommerce.entity.Seller;
import com.ayushjainttn.bootcampproject.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    public User findByUserEmailIgnoreCase(String userEmail);
    @Modifying
    @Query(value = "UPDATE user_table SET invalid_attempt_count = :count WHERE user_email = :userEmail", nativeQuery = true)
    public void updateInvalidAttemptCount(@Param("userEmail") String userEmail, @Param("count") int count);

    @Modifying
    @Query(value = "UPDATE user_table SET user_password = :password, password_update_date = :date WHERE user_email = :email", nativeQuery = true)
    public void updatePassword(@Param("password") String encodedPassword, @Param("date") Date date, @Param("email") String userEmail);

    @Transactional
    @Modifying
    @Query(value = "UPDATE user_table SET is_active = :value WHERE user_id = :userId", nativeQuery = true)
    public void updateIsActiveStatus(@Param("value") Boolean value, @Param("userId") int userId);

    @Query(value = "SELECT * FROM user_table WHERE is_active=:isActive AND ",nativeQuery = true)
    public List<User> findSellerByActivationStatus(@Param("isActive") boolean isActive);
}
