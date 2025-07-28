package com.example.nvt.service;

import com.example.nvt.enumeration.RequestStatus;
import com.example.nvt.model.HouseholdRequest;
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

    @Value("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")
    private String SENDGRID_API_KEY;

    private static final String VERIFICATION_TEMPLATE_VERIFY_EMAIL_ID = "d-e707779b69ff472aa8e1a3f9c2773d5d";
    private static final String REQUEST_STATUS_UPDATE_TEMPLATE_ID = "d-a66753ad11da4e6fa4891c3d49ece978";

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

    public void sendRequestUpdate(String email, String firstName, String address, String status, String reason) {
        Email from = new Email("mobilnebackendtest@gmail.com");

        Email to = new Email(email);
        System.out.println("Sent email to: " + email);

        Personalization personalization = new Personalization();
        personalization.addTo(to);
        Mail mail = new Mail();
        mail.setFrom(from);

        String subject = "Request status update";
        personalization.addDynamicTemplateData("firstName", firstName);
        personalization.addDynamicTemplateData("address", address);
        personalization.addDynamicTemplateData("requestStatus", status);
        if(reason != null ) personalization.addDynamicTemplateData("reason", "Reason: " + reason);

        mail.setTemplateId(REQUEST_STATUS_UPDATE_TEMPLATE_ID);

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
