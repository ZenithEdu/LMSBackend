package com.MiniLms.LMSBackend.service.emailService;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordEmailServiceImpl extends EmailService implements IForgotPasswordEmailService{
    public ForgotPasswordEmailServiceImpl(JavaMailSender mailSender) {
        super(mailSender);
    }

    @Override
    public void sendForgotPasswordEmail(String email, String token) {
        String resetLink = IForgotPasswordEmailService.RESET_LINK + token;
        String subject = IForgotPasswordEmailService.SUBJECT;
        String body =
            IForgotPasswordEmailService.BODY1 + resetLink +IResetPasswordFirstTimeEmailService.BODY2;
        sendEmail(email,subject,body);
    }
}
