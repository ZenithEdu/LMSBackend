package com.MiniLms.LMSBackend.service.emailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService{

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    protected void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(fromEmail);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
