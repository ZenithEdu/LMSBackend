package com.MiniLms.LMSBackend.service.emailService;

public interface IResetPasswordFirstTimeEmailService {
    String RESET_LINK = "http://localhost:8081/reset-password"+"?token=";
    String SUBJECT = "Your LMS Account Credentials";
    String BODY1 = """
        Your account has been created.
        
        Temporary Password:\s""";
    String BODY2 = """
        
        Please use this password to login and reset your password immediately.
        
        You can also reset your password directly using this link:
        """;
    String BODY3 = """
        
        
        This link will expire in 24 hours.""";
    void sendResetEmail(String email,String tempPassword, String token);
}
