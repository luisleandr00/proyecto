package com.wolfiez.wallpaper.service;

import com.wolfiez.wallpaper.entity.User;
import com.wolfiez.wallpaper.repository.UserRepository;
import com.wolfiez.wallpaper.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * Loads user-specific data for authentication and authorization.
 *
 * @author luis
 * @version 1.0
 * @since 25-11-2024
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user by their email address for authentication.
     *
     * @param email User's email address
     * @return UserDetails object containing user authentication information
     * @throws UsernameNotFoundException If no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("USER")),
                user.getId(),
                user.getName(),
                user.getProfileImage()
        );
    }
}