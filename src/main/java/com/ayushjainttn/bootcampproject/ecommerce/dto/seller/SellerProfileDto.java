package com.ayushjainttn.bootcampproject.ecommerce.dto.seller;

import com.ayushjainttn.bootcampproject.ecommerce.dto.user.UserProfileDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDetailDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerProfileDto extends UserProfileDto {
    private String sellerGstNumber;
    private String sellerCompanyName;
    private String sellerCompanyContact;
    private AddressDetailDto address;
}
