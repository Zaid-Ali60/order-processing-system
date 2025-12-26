package com.cashinvoice.orderprocessing.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, UserDetails> users = new HashMap<>();
    
    public CustomUserDetailsService() {
        // Initialize with default users
        // Password: admin123
        users.put("admin", User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build());
        
        // Password: user123
        users.put("CUST1001", User.builder()
                .username("CUST1001")
                .password(passwordEncoder.encode("user123"))
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build());
        
        users.put("CUST1002", User.builder()
                .username("CUST1002")
                .password(passwordEncoder.encode("user123"))
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build());
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}
