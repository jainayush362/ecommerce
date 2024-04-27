package com.ayushjainttn.bootcampproject.ecommerce.dto.password;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PasswordDto {
    @NotBlank(message = "{password.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_PASSWORD, message = "{password.valid}")
    private String newPassword;

    @NotBlank(message = "{confirmPassword.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_PASSWORD, message = "{password.valid}")
    private String confirmNewPassword;
}
