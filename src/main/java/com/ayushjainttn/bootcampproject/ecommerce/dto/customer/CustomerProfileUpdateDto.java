package com.ayushjainttn.bootcampproject.ecommerce.dto.customer;

import com.ayushjainttn.bootcampproject.ecommerce.dto.user.UserProfileUpdateDto;
import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CustomerProfileUpdateDto extends UserProfileUpdateDto {
    @Pattern(regexp = GlobalExpressions.RGX_MOBILE_NUMBER, message = "{contact.valid}")
    private String customerContact;
}
