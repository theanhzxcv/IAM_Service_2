package com.theanh.iamservice.IAM_Service_2.Services;

import jakarta.mail.MessagingException;

public interface IEmailService {

    void sendResetPasswordEmail(String email, String token) throws MessagingException;
}
