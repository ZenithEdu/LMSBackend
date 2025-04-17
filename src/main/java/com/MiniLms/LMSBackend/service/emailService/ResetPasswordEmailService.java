package com.MiniLms.LMSBackend.service.emailService;

import org.springframework.mail.javamail.JavaMailSender;

public class ResetPasswordEmailService extends EmailService {

    public ResetPasswordEmailService(JavaMailSender mailSender) {
        super(mailSender);
    }
    private static void sendResetEmail(String email,String token){

    }
}
