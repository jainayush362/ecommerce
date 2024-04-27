package com.ayushjainttn.bootcampproject.ecommerce.dto.customer;

import com.ayushjainttn.bootcampproject.ecommerce.dto.user.UserDetailDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDetailDto extends UserDetailDto {
    @JsonIgnore
    private String customerContact;
}
