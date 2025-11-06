package com.project2.loginsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to login, signup, and static resources (css, js)
                        .requestMatchers("/loginsystem/login",
                                "/loginsystem/signup",
                                "/loginsystem/send-otp",
                                "/loginsystem/verify-otp",
                                "/loginsystem/forgot-password",  // <-- ADD THIS
                                "/loginsystem/reset-password",
                                "/css/**",
                                "/js/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // Use our custom login page
                        .loginPage("/loginsystem/login")
                        // Spring Security will handle the POST to this URL
                        .loginProcessingUrl("/loginsystem/login")
                        // Where to go after a successful login
                        .defaultSuccessUrl("/", true)
                        // Where to go if login fails
                        .failureUrl("/loginsystem/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        // The URL to trigger a logout
                        .logoutUrl("/logout")
                        // Where to go after logging out
                        .logoutSuccessUrl("/loginsystem/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}
