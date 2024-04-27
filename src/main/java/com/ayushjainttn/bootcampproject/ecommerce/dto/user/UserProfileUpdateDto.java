package com.ayushjainttn.bootcampproject.ecommerce.dto.user;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserProfileUpdateDto {
    @Pattern(regexp = GlobalExpressions.RGX_ALPHABETS, message = "{firstName.valid}")
    private String userFirstName;

    @Pattern(regexp = GlobalExpressions.RGX_ALPHABETS, message = "{middleName.valid}")
    private String userMiddleName;

    @Pattern(regexp = GlobalExpressions.RGX_ALPHABETS, message = "{lastName.valid}")
    private String userLastName;
}
