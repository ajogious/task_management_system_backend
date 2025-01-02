package com.ajogious.task_management_backend.security;

import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.repositories.AuthRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User savedUser = authRepository.findUserByEmail(email);
        if (savedUser == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Checking if the user account is active
        if (!savedUser.isActive()) {
            throw new DisabledException("Account is suspended. Please contact admin.");
        }

        return new UserPrinciple(savedUser);
    }
}
