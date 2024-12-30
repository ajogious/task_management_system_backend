package com.ajogious.task_management_backend.security;

import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.repositories.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User savedUser = userRepository.findUserByEmail(email);
        if (savedUser == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Check if the user account is active
        if (!savedUser.isActive()) {
            throw new DisabledException("Account is suspended. Please contact admin.");
        }

        return new UserPrinciple(savedUser);
    }
}
