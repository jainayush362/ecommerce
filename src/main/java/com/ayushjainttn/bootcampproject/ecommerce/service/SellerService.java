package com.ayushjainttn.bootcampproject.ecommerce.service;

import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDetailDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.password.PasswordUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerProfileDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerProfileUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerRegisterationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface SellerService {
    public ResponseEntity registerNewSeller(SellerRegisterationDto sellerDto);
    public ResponseEntity retrieveSellerProfile(String path, Principal principal);
    public ResponseEntity updateSellerProfileDetails(Principal principal, SellerProfileUpdateDto sellerProfileUpdateDto);
    public ResponseEntity updateSellerCompanyAddress(Principal principal, AddressDetailDto addressDetailDto);
}
