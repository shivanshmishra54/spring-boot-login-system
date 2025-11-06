package com.project2.loginsystem.controller;

import com.project2.loginsystem.entity.User;
import com.project2.loginsystem.repository.RolesRespository;
import com.project2.loginsystem.repository.UserRepository;
import com.project2.loginsystem.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;

@Controller
public class AppController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRespository rolesRespository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;


    @GetMapping("/loginsystem/login")
    public String login() {

        return "login";
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
        model.addAttribute("roles", roles);
        return "home";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin";
    }

    @GetMapping("/loginsystem/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    // --- Endpoint 1: Send the OTP (No change) ---
    @PostMapping("/loginsystem/send-otp")
    @ResponseBody
    public Map<String, Object> sendOtp(@RequestParam("email") String email, HttpSession session) {
        if (userRepository.existsByEmail(email)) { //
            return Map.of("success", false, "message", "Email is already registered.");
        }
        try {
            String otp = emailService.generateOtp();
            emailService.sendOtpEmail(email, otp);
            session.setAttribute("signupOtp", otp);
            session.setAttribute("signupOtpTime", LocalDateTime.now());
            session.setAttribute("signupEmail", email);
            return Map.of("success", true, "message", "OTP sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "Error sending OTP. Please try again.");
        }
    }

    // --- NEW Endpoint 2: Verify the OTP ---
    @PostMapping("/loginsystem/verify-otp")
    @ResponseBody
    public Map<String, Object> verifyOtp(@RequestParam("otp") String otp, HttpSession session) {
        String sessionOtp = (String) session.getAttribute("signupOtp");
        LocalDateTime sessionOtpTime = (LocalDateTime) session.getAttribute("signupOtpTime");

        if (sessionOtp == null || sessionOtpTime == null) {
            return Map.of("success", false, "message", "No OTP found. Please send again.");
        }

        // Check if expired (5 minutes)
        if (sessionOtpTime.isBefore(LocalDateTime.now().minusMinutes(5))) {
            return Map.of("success", false, "message", "OTP has expired. Please send again.");
        }

        if (otp.equals(sessionOtp)) {
            // --- SUCCESS ---
            // Mark the email as verified in the session
            session.setAttribute("isEmailVerified", true);
            session.removeAttribute("signupOtp"); // Clear the OTP
            session.removeAttribute("signupOtpTime");
            return Map.of("success", true, "message", "Email verified successfully!");
        } else {
            return Map.of("success", false, "message", "Invalid OTP. Please try again.");
        }
    }


    // --- MODIFIED Endpoint 3: Final Signup ---
    @PostMapping("/loginsystem/signup")
    public String processSignup(@ModelAttribute User user, HttpSession session) {

        // --- NEW VALIDATION ---
        // 1. Check if the email was actually verified in the session
        Boolean isEmailVerified = (Boolean) session.getAttribute("isEmailVerified");
        String sessionEmail = (String) session.getAttribute("signupEmail");

        if (isEmailVerified == null || !isEmailVerified || sessionEmail == null) {
            return "redirect:/loginsystem/signup?otp_error=Please verify your email first.";
        }

        // 2. Check if they changed the email *after* verifying
        if (!user.getEmail().equals(sessionEmail)) {
            return "redirect:/loginsystem/signup?otp_error=Email does not match verified email.";
        }

        // 3. Check for existing username (your original check)
        if (userRepository.existsByUsername(user.getUsername())) {
            return "redirect:/loginsystem/signup?user_error";
        }

        // --- ALL CHECKS PASSED ---
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Collections.singleton(rolesRespository.findByRoleNames("ROLE_USER")));
        user.setEnabled(true);
        userRepository.save(user);

        // 6. Clear all session attributes
        session.removeAttribute("isEmailVerified");
        session.removeAttribute("signupEmail");

        return "redirect:/loginsystem/login?signup_success";
    }

    /**
     * Shows the 'Forgot Password' email entry page.
     */
    @GetMapping("/loginsystem/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    /**
     * Processes the 'Forgot Password' email submission.
     */
    @PostMapping("/loginsystem/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email) {

        var userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // User not found, but we redirect to 'success'
            // to prevent attackers from guessing which emails are registered.
            return "redirect:/loginsystem/forgot-password?success";
        }

        User user = userOptional.get();

        // 1. Generate a secure token
        String token = UUID.randomUUID().toString();

        // 2. Set token and expiry time (e.g., 1 hour from now)
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));

        // 3. Save the updated user
        userRepository.save(user);

        // 4. Send the email asynchronously
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return "redirect:/loginsystem/forgot-password?success";
    }

    /**
     * Shows the new password entry form if the token is valid.
     */
    @GetMapping("/loginsystem/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {

        var userOptional = userRepository.findByResetPasswordToken(token);

        if (userOptional.isEmpty()) {
            return "redirect:/loginsystem/login?error=Invalid token";
        }

        User user = userOptional.get();

        // Check if token is expired
        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            // Clear the token and save
            user.setResetPasswordToken(null);
            user.setResetPasswordTokenExpiry(null);
            userRepository.save(user);
            return "redirect:/loginsystem/login?error=Expired token";
        }

        // Token is valid, show the form
        model.addAttribute("token", token);
        return "reset-password";
    }

    /**
     * Processes the new password submission.
     */
    @PostMapping("/loginsystem/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password) {

        var userOptional = userRepository.findByResetPasswordToken(token);

        if (userOptional.isEmpty()) {
            return "redirect:/loginsystem/login?error=Invalid token";
        }

        User user = userOptional.get();

        // Check if token is expired (again)
        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            return "redirect:/loginsystem/login?error=Expired token";
        }

        // --- Token is valid and not expired ---
        // 1. Set the new password (encoded)
        user.setPassword(passwordEncoder.encode(password));

        // 2. Clear the token fields
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        // 3. Save the user
        userRepository.save(user);

        return "redirect:/loginsystem/login?reset_success";
    }
}