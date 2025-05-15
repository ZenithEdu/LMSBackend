package com.MiniLms.LMSBackend.service.emailService;

public interface IForgotPasswordEmailService {
    String RESET_LINK = "http://localhost:5173/changePassword" + "?token=";
    String SUBJECT = "LMS Password Reset Request";
    String BODY1 = """
        We received a request to reset your LMS account password.
        
        If you made this request, please use the link below to reset your password:
        """;
    String BODY2 = """
        
        If you did not request a password reset, you can safely ignore this email.
        
        This link will expire in 24 hours for security reasons.
        """;

    void sendForgotPasswordEmail(String email, String token);
}
