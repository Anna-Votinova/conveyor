package com.neoflex.dossier.mail;

import com.neoflex.dossier.dto.DocumentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String conveyorAddress;

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine thymeleafTemplateEngine;

    public void sendSimpleMessage(String to, String subject, String text) {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(conveyorAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);
    }

    public void sendComplexMessage(String to, String subject, DocumentDTO documentDTO) throws MessagingException {
        Context context = getContext(documentDTO);
        String htmlBody = thymeleafTemplateEngine.process("template-thymeleaf.html", context);
        sendHtmlMessage(to, subject, htmlBody);
    }

    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        emailSender.send(message);
    }

    private Context getContext(DocumentDTO documentDTO) {
        String recipientName;

        if (documentDTO.middleName() != null) {
            recipientName = documentDTO.lastName() + " " + documentDTO.firstName() + " " + documentDTO.middleName();
        } else {
            recipientName = documentDTO.lastName() + " " + documentDTO.firstName();
        }

        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("amount", documentDTO.amount());
        context.setVariable("term", documentDTO.term());
        context.setVariable("monthlyPayment", documentDTO.monthlyPayment());
        context.setVariable("rate", documentDTO.rate());
        context.setVariable("psk", documentDTO.psk());
        context.setVariable("isInsuranceEnabled", documentDTO.isInsuranceEnabled());
        context.setVariable("isSalaryClient", documentDTO.isSalaryClient());
        context.setVariable("paymentSchedule", documentDTO.paymentSchedule());

        return context;
    }
}
