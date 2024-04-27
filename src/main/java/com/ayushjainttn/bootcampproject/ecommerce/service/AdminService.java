package com.ayushjainttn.bootcampproject.ecommerce.service;

import com.ayushjainttn.bootcampproject.ecommerce.dto.seller.SellerDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    public Page findAllSellers(int pageOffset, int pageSize, String sortProperty, Optional<String> email, String sortDirection);
    public Page findAllCustomers(int pageOffset, int pageSize, String sortProperty, Optional<String> email, String sortDirection);
    public ResponseEntity activateUser(int userId);
    public ResponseEntity deactivateUser(int userId);
    public List<SellerDetailDto> getAllInactiveSeller();
    public List<SellerDetailDto> getAllActiveSeller();
}
