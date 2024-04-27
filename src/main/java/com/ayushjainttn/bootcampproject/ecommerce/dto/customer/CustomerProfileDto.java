package com.ayushjainttn.bootcampproject.ecommerce.dto.customer;

import com.ayushjainttn.bootcampproject.ecommerce.dto.user.UserProfileDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerProfileDto extends UserProfileDto {
    private String customerContact;
}
