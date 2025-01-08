package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.EmailImp;

import com.theanh.iamservice.IAM_Service_2.Services.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImp implements IEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendResetPasswordEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("To reset your password, click the link below: ");
        mimeMessageHelper.setText("""
        <div>
          <a href="http://localhost:8081/iam/users/reset-password?token=%s" target="_blank"></a>
        </div>
        """.formatted(token), true);

        mailSender.send(message);
    }
}
