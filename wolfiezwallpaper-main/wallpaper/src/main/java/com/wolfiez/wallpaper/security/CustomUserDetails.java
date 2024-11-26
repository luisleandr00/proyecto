package com.wolfiez.wallpaper.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Represents a custom implementation of Spring Security's UserDetails interface.
 * Provides extended user authentication and authorization information.
 *
 * This class stores additional user details beyond standard Spring Security user properties,
 * such as user ID, name, and profile image URL.
 *
 * @author luis
 * @version 1.0
 * @since 25-11-2024
 */
@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Long id;
    private final String name;
    private final String profileImage;

    /**
     * Returns the email address used as the username for authentication.
     *
     * @return User's email address
     */
    @Override
    public String getUsername() {
        return email;
    }
    /**
     * Determines if the user's account is non-expired.
     * Always returns true in this implementation.
     *
     * @return Always true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    /**
     * Determines if the user's account is non-locked.
     * Always returns true in this implementation.
     *
     * @return Always true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Determines if the user's credentials are non-expired.
     * Always returns true in this implementation.
     *
     * @return Always true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Determines if the user's account is enabled.
     * Always returns true in this implementation.
     *
     * @return Always true
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}