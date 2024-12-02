package com.example.nvt.service;

import com.example.nvt.model.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.sendgrid.api-key}")
    private String SENDGRID_API_KEY;

    private static final String VERIFICATION_TEMPLATE_VERIFY_EMAIL_ID = "d-3d2f42ee76ed4904bb916951f3471b95";

    public void sendVerificationEmail(User user) {
        Email from = new Email("mobilnebackendtest@gmail.com");

        String toEmail = user.getEmail();

        Email to = new Email(toEmail);
        System.out.println("Sent email to: " + toEmail);

        Personalization personalization = new Personalization();
        personalization.addTo(to);
        Mail mail = new Mail();
        mail.setFrom(from);

        String subject = "Verify email address";
        personalization.addDynamicTemplateData("firstName", user.getFirstName());
        personalization.addDynamicTemplateData("verificationLink", "http://localhost:8080/api/v1/auth/activate/".concat(user.getVerification().getVerificationCode()));
        mail.setTemplateId(VERIFICATION_TEMPLATE_VERIFY_EMAIL_ID);

        mail.setSubject(subject);
        mail.addPersonalization(personalization);
        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
