package com.ayushjainttn.bootcampproject.ecommerce.dto.address;

import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class AddressDto {

    @NotBlank(message = "{city.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_ADDRESS_CITY, message = "{city.valid}")
    private String city;

    @NotBlank(message = "{state.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_ADDRESS_STATE, message = "{state.valid}")
    private String state;

    @NotBlank(message = "{country.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_ADDRESS_COUNTRY, message = "{country.valid}")
    private String country;

    @NotBlank(message = "{postalCode.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_ADDRESS_POSTAL_CODE, message = "{postalCode.valid}")
    @Length(min = 6, max = 6, message = "{postalCode.length}")
    private String postalCode;

    @NotBlank(message = "{addressLine.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_ADDRESS_LINE, message = "{addressLine.valid}")
    private String addressLine;

    @NotBlank(message = "{label.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_ADDRESS_LABEL, message = "{label.valid}")
    private String label;
}
