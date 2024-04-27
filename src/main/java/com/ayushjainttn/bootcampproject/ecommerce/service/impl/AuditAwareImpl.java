package com.ayushjainttn.bootcampproject.ecommerce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
@Slf4j
public class AuditAwareImpl implements AuditorAware<String> {
    /**
     * Method returns currentLoggedIn user. Used for auditing purposes
     * @return currentLoggedInUser
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        log.info("----inside getCurrentAuditor() method----");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("----current logged in user is none-> SYSTEM GENERATED. method executed success----");
            return Optional.ofNullable("SYSTEM_GENERATED");
        }
        log.info("----current logged in user is present. method executed success----");
        return Optional.ofNullable(authentication.getName());
    }
}
