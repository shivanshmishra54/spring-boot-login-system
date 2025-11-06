package com.project2.loginsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name", updatable = true, nullable = false)
    private String name;

    @Column(name = "username", updatable = false,nullable = false, unique = true)
    private String username;

    @Column(name = "user_phone", updatable = true, nullable = false)
    private String phone;

    @Column(name = "email", updatable = false, nullable = false, unique = true)
    private String email;

    @Column(name = "password", updatable = true, nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"
            )
    )
    private Set<Roles> role = new HashSet<>();

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetPasswordTokenExpiry;

    @Column(name = "enabled", columnDefinition = "boolean default false")
    private boolean enabled;
}