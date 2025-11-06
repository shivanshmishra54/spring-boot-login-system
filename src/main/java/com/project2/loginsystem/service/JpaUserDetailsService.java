package com.project2.loginsystem.service;

import com.project2.loginsystem.entity.User;
import com.project2.loginsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service // <-- This annotation is critical!
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Fetch your User entity
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Convert your Set<Roles> into a List<GrantedAuthority>
        // You need to get the user's roles (user.getRole())
        // then stream them, and map each 'Roles' entity to its name (role.getRoleNames())
        var authorities = user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleNames()))
                .collect(Collectors.toList());

        // 3. Return Spring Security's User object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(), // <-- PASS THE ENABLED FLAG HERE
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities // Pass the correct authorities list
        );
    }
}