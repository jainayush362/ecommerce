package com.ayushjainttn.bootcampproject.ecommerce.service;

import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.email.EmailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.password.PasswordDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.password.PasswordUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.entity.Address;
import com.ayushjainttn.bootcampproject.ecommerce.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface UserService {
    public void credentialsExpired(User user);
    public void resetInvalidAttempts(String email);
    public void incrementInvalidAttempts(User user);
    public void accountLock(User user);
    public boolean accountUnlock(User user);
    public boolean isUserAdmin(User user);
    public User retrieveUserProfile(String path, User user);
    public User findUserByEmail(String email);
    public Address updateUserAddressDetails(Address address, AddressDetailDto addressDetailDto);
    public ResponseEntity updatePassword(Principal principal, PasswordUpdateDto passwordUpdateDto);
    public ResponseEntity resetPasswordToken(EmailDto emailDto);
    public ResponseEntity resetPassword(PasswordDto passwordDto, String token);
    public ResponseEntity updateUserProfileImage(String path, MultipartFile imageFile, Principal principal);
}
