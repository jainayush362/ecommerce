package com.ayushjainttn.bootcampproject.ecommerce.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class AppUser implements UserDetails {
    private String username;
    private String password;
    private boolean isEnabled;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private List<GrantAuthorityImpl> grantAuthorityList;

    public AppUser(String username,
                   String password,
                   Boolean isEnabled,
                   Boolean isAccountNonExpired,
                   Boolean isAccountNonLocked,
                   Boolean isCredentialsNonExpired,
                   List<GrantAuthorityImpl> grantAuthorityList) {

        this.username = username;
        this.password = password;
        this.isEnabled = isEnabled;
        this.isAccountNonExpired = !isAccountNonExpired;
        this.isAccountNonLocked = !isAccountNonLocked;
        this.isCredentialsNonExpired = !isCredentialsNonExpired;
        this.grantAuthorityList = grantAuthorityList;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantAuthorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
