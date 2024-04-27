package com.ayushjainttn.bootcampproject.ecommerce.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    public void sendEmail(SimpleMailMessage simpleMailMessage);
    public SimpleMailMessage sendAccountRelatedUpdateEmail(String userEmail, String message, String subject);
}
