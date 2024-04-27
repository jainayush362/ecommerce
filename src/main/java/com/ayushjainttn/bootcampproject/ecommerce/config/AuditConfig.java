package com.ayushjainttn.bootcampproject.ecommerce.config;

import com.ayushjainttn.bootcampproject.ecommerce.service.impl.AuditAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditConfig {
    @Bean
    public AuditorAware auditorAware(){
        return new AuditAwareImpl();
    }
}
