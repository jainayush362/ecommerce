package com.ayushjainttn.bootcampproject.ecommerce.dto.user;

import com.ayushjainttn.bootcampproject.ecommerce.payload.UserImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDto {
    private UserImage image;
    private Long userId;
    private String userFirstName;
    private String userMiddleName;
    private String userLastName;
    private String userEmail;
    private Boolean userIsActive;
}
