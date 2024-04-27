package com.ayushjainttn.bootcampproject.ecommerce.dto.email;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class EmailDto {
    @NotBlank(message = "{emailId.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_EMAIL, flags = Pattern.Flag.CASE_INSENSITIVE, message = "{emailId.valid}")
    private String userEmail;
}
