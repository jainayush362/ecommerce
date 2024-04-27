package com.ayushjainttn.bootcampproject.ecommerce.dto.customer;

import com.ayushjainttn.bootcampproject.ecommerce.dto.user.UserRegisterationDto;
import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CustomerRegisterationDto extends UserRegisterationDto {
    @NotBlank(message = "{contact.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_MOBILE_NUMBER, message = "{contact.valid}")
    private String customerContact;
}
