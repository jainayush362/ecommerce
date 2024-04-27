package com.ayushjainttn.bootcampproject.ecommerce.security;

import com.ayushjainttn.bootcampproject.ecommerce.entity.User;
import com.ayushjainttn.bootcampproject.ecommerce.exceptions.GenericActivationException;
import com.ayushjainttn.bootcampproject.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CustomAuthenticationManager implements AuthenticationManager {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        log.info("CustomAuthenticationManager::authenticate execution started.");
        log.debug("CustomAuthenticationManager::authenticate authenticating credentials, generating access token");

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userService.findUserByEmail(username);

        if(user==null){
            log.error("Exception occurred while authenticating");
            throw new UsernameNotFoundException("Invalid credentials");
        }

        userService.credentialsExpired(user);

        if(user.getUserIsLocked().booleanValue()){
            if(userService.accountUnlock(user)){
                log.debug("CustomAuthenticationManager::account unlocked");
            }else{
                log.error("Exception occurred while authenticating");
                throw new LockedException("Account is Locked due to 3 invalid login attempts.");
            }
        }
        if (user.getUserIsExpired().booleanValue()){
            log.error("Exception occurred while authenticating");
            throw new CredentialsExpiredException("Credentials Expired! Reset your Password");
        }
        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            log.debug("CustomAuthenticationManager::authenticate invalid attempt");
            int counter = user.getUserInvalidAttemptCount()+1;
            if(counter < 3){
                userService.incrementInvalidAttempts(user);
            } else{
                userService.accountLock(user);
                log.debug("CustomAuthenticationManager::authenticate account locked");
                throw new LockedException("Account is Locked due to 3 invalid login attempts.");
            }
            log.error("Exception occurred while authenticating");
            throw new BadCredentialsException("Invalid Credentials. 3 Attempts allowed. Current Attempt : "+counter);
        }
        if(!user.getUserIsActive().booleanValue()){
            userService.resetInvalidAttempts(user.getUserEmail());
            log.error("Exception occurred while authenticating");
            throw new AccountStatusException("Account Inactive") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getAuthority()));
        userService.resetInvalidAttempts(user.getUserEmail());
        log.debug("CustomAuthenticationManager::authenticate credentials authenticated ");
        log.info("CustomAuthenticationManager::authenticate execution ended.");
        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }
}