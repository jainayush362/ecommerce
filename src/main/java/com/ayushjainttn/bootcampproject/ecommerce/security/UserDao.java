package com.ayushjainttn.bootcampproject.ecommerce.security;

import com.ayushjainttn.bootcampproject.ecommerce.entity.User;
import com.ayushjainttn.bootcampproject.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

@Repository
public class UserDao{
    @Autowired
    private UserRepository userRepository;

    AppUser loadUserByUsername(String email){
        User user = userRepository.findByUserEmailIgnoreCase(email);
        if(user!=null){
            System.out.println(user.getUserIsActive());
            return new AppUser(user.getUserEmail(), user.getUserPassword(), user.getUserIsActive(), user.getUserIsDeleted(), user.getUserIsLocked(), user.getUserIsExpired(), Arrays.asList(new GrantAuthorityImpl(user.getRole().getAuthority())));
        }else{
            throw new BadCredentialsException("No user exists with given Credentials");
        }
    }
}
