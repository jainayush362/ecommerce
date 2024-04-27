package com.ayushjainttn.bootcampproject.ecommerce.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class UserDetailDto extends RepresentationModel{
    private Long userId;
    @JsonIgnore
    private String userFirstName;
    @JsonIgnore
    private String userMiddleName;
    @JsonIgnore
    private String userLastName;
    private String userFullName;
    private String userEmail;
    private Boolean userIsActive;

    public String getUserFullName() {
        String name = "";
        if(userFirstName!=null) name=name+userFirstName.trim()+" ";
        if(userMiddleName!=null && !userMiddleName.equals("")) name=name+userMiddleName.trim()+" ";
        if(userLastName!=null) name=name+userLastName.trim();
        return name;
    }
}
