package com.ayushjainttn.bootcampproject.ecommerce.dto.user;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserRegisterationDto {

    @NotBlank(message = "{firstName.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_ALPHABETS, message = "{firstName.valid}")
    private String userFirstName;

    @Pattern(regexp = GlobalExpressions.RGX_ALPHABETS, message = "{middleName.valid}")
    private String userMiddleName;

    @NotBlank(message = "{lastName.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_ALPHABETS, message = "{lastName.valid}")
    private String userLastName;

    @NotBlank(message = "{emailId.notBlank}")
    @Email(regexp = GlobalExpressions.RGX_EMAIL, flags = Pattern.Flag.CASE_INSENSITIVE, message = "{emailId.valid}")
    private String userEmail;

    @NotBlank(message = "{password.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_PASSWORD, message = "{password.valid}")
    private String userPassword;

    @NotBlank(message = "{confirmPassword.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_PASSWORD, message = "{password.valid}")
    private String userConfirmPassword;
}
