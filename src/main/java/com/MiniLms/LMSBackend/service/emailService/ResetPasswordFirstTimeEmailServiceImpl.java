package com.MiniLms.LMSBackend.service.emailService;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordFirstTimeEmailServiceImpl extends EmailService implements IResetPasswordFirstTimeEmailService{

    public ResetPasswordFirstTimeEmailServiceImpl(JavaMailSender mailSender) {
        super(mailSender);
    }

    @Override
    public void sendResetEmail(String email, String tempPassword, String token) {
        String resetLink = IResetPasswordFirstTimeEmailService.RESET_LINK + token;
        String subject = IResetPasswordFirstTimeEmailService.SUBJECT;
        String body =
            IResetPasswordFirstTimeEmailService.BODY1 + tempPassword + IResetPasswordFirstTimeEmailService.BODY2 + resetLink + IResetPasswordFirstTimeEmailService.BODY3;
        sendEmail(email,subject,body);
    }
}
