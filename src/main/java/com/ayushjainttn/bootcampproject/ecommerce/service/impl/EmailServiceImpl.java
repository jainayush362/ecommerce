package com.ayushjainttn.bootcampproject.ecommerce.service.impl;

import com.ayushjainttn.bootcampproject.ecommerce.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderEmailId;
    private SimpleMailMessage mail = new SimpleMailMessage();

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Sends email asynchronously
     * @param simpleMailMessage
     */
    @Async
    public void sendEmail(SimpleMailMessage simpleMailMessage){
        log.info("----inside sendEmail() method----");
        javaMailSender.send(simpleMailMessage);
        log.info("----mail sent. method executed success----");
    }

    /**
     * Method returns a SimpleMailMessage Object that is required by sendEmail() to send emails
     * @param userEmail
     * @param message
     * @param subject
     * @return SimpleMailMessage Object
     */
    public SimpleMailMessage sendAccountRelatedUpdateEmail(String userEmail, String message, String subject){
        log.info("----inside sendAccountRelatedUpdateEmail() method----");
        mail.setFrom(senderEmailId);
        mail.setTo(userEmail);
        mail.setSubject(subject);
        mail.setText(message);
        log.info("----SimpleMailMessage object returned. method executed success----");
        return mail;
    }
}
