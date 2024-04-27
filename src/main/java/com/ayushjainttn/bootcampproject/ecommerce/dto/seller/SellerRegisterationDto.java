package com.ayushjainttn.bootcampproject.ecommerce.dto.seller;

import com.ayushjainttn.bootcampproject.ecommerce.dto.user.UserRegisterationDto;
import com.ayushjainttn.bootcampproject.ecommerce.dto.address.AddressDto;
import com.ayushjainttn.bootcampproject.ecommerce.utils.GlobalExpressions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SellerRegisterationDto extends UserRegisterationDto {
    @NotBlank(message = "{gst.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_GST_NUMBER, message = "{gst.valid}")
    private String sellerGstNumber;

    @NotBlank(message = "{companyName.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_COMPANY_NAME, message = "{companyName.valid}")
    private String sellerCompanyName;

    @NotBlank(message = "{contact.notBlank}")
    @Pattern(regexp = GlobalExpressions.RGX_MOBILE_NUMBER, message = "{contact.valid}")
    private String sellerCompanyContact;

    @Valid
    @NotNull
    private AddressDto address;
}
