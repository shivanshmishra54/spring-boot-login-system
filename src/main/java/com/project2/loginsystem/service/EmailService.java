package com.project2.loginsystem.service;

import jakarta.mail.MessagingException; // <-- Import
import jakarta.mail.internet.MimeMessage; // <-- Import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper; // <-- Import
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine; // <-- Import
import org.thymeleaf.context.Context; // <-- Import

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine; // <-- Inject Thymeleaf's engine

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public String generateOtp(){
        Random random= new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Sends the OTP to the user's email using the HTML template.
     */
    @Async("emailTaskExecutor") // <-- This still runs it on a background thread
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            logger.info("Sending HTML OTP email to " + toEmail + " on thread: " + Thread.currentThread().getName());

            // --- 1. Create the Thymeleaf Context ---
            // This is what passes variables like "otp" to the HTML
            Context context = new Context();
            context.setVariable("otp", otp);

            // --- 2. Process the HTML Template ---
            // This line "builds" the HTML string from your template file
            String htmlContent = templateEngine.process("email-otp-template", context);

            // --- 3. Create the MimeMessage (for HTML) ---
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your Verification Code");
            helper.setText(htmlContent, true); // <-- true = this is HTML

            // You can also set a 'from' address (optional, but professional)
            // helper.setFrom("no-reply@yourcompany.com", "Your Application Name");

            // --- 4. Send the Email ---
            mailSender.send(mimeMessage);

            logger.info("Successfully sent HTML OTP to " + toEmail);

        } catch (MessagingException e) {
            // Log any errors that happen in this new thread
            logger.error("Error sending HTML OTP email to " + toEmail, e);
        }
    }

    /**
     * Sends the Password Reset link to the user's email.
     */
    @Async("emailTaskExecutor")
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            logger.info("Sending Password Reset email to " + toEmail);

            // --- 1. Create the Full Reset Link ---
            // For production, you would get this URL from application.properties
            String resetLink = "http://localhost:8080/loginsystem/reset-password?token=" + token;

            // --- 2. Create the Thymeleaf Context ---
            Context context = new Context();
            context.setVariable("resetLink", resetLink);

            // --- 3. Process the HTML Template ---
            String htmlContent = templateEngine.process("email-reset-template", context);

            // --- 4. Create and Send the MimeMessage ---
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your Password Reset Request");
            helper.setText(htmlContent, true); // true = this is HTML
            // helper.setFrom("no-reply@yourcompany.com", "Your Application Name");

            mailSender.send(mimeMessage);

            logger.info("Successfully sent password reset email to " + toEmail);

        } catch (MessagingException e) {
            logger.error("Error sending password reset email to " + toEmail, e);
        }
    }
}