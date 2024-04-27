package com.ayushjainttn.bootcampproject.ecommerce.service;

import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.customer.CustomerProfileUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.customer.CustomerRegisterationDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.email.EmailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.password.PasswordUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.entity.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface CustomerService {
    public ResponseEntity registerNewCustomer(CustomerRegisterationDto customerDto);
    public void generateCustomerConfirmationToken(Customer customer);
    public ResponseEntity checkConfirmationToken(String token);
    public ResponseEntity resendConfirmationToken(EmailDto emailDto);
    public ResponseEntity retrieveCustomerProfile(String path, Principal principal);
    public ResponseEntity updateCustomerProfileDetails(Principal principal, CustomerProfileUpdateDto customerProfileUpdateDto);
    public ResponseEntity updateCustomerAddress(Principal principal, int addressId, AddressDetailDto addressDetailDto);
    public ResponseEntity retrieveCustomerAddress(Principal principal);
    public ResponseEntity addCustomerAddress(Principal principal, AddressDto addressDto);
    public ResponseEntity removeCustomerAddress(Principal principal, int addressId);
}
