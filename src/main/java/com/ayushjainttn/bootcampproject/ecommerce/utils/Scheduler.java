package com.ayushjainttn.bootcampproject.ecommerce.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ayushjainttn.bootcampproject.ecommerce.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Scheduler {

    @Autowired
    private EmailService emailService;
    @Autowired
    private MessageSource messageSource;
    private Locale locale = LocaleContextHolder.getLocale();
    @Value("${service.url}")
    private String baseUrl;
    @Value("${admin.email}")
    private String adminEmail;
    //reference - https://spring.io/blog/2020/11/10/new-in-spring-5-3-improved-cron-expressions

    @Scheduled(cron = "@daily") //@daily means at 12:00 a.m. everyday
    public void runScheduler() {
        log.info("----Scheduler started executing ----");
        String subject = messageSource.getMessage("email.admin.scheduler.subject",null,locale);
        String message = messageSource.getMessage("email.admin.scheduler.seller.activate.message",null,locale)+" \n" +
                baseUrl+"/admin/view/inactive/seller"+"\n"+
                messageSource.getMessage("email.admin.scheduler.product.activate.message",null,locale)+" \n" +
                baseUrl+"/admin/view/inactive/product";
        emailService.sendEmail(emailService.sendAccountRelatedUpdateEmail(adminEmail,message,subject));
        log.info("----Scheduler sent email to admin success ----");
        log.info("----Scheduler successfully executed----");
    }
}