package com.cms.util;

import com.cms.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SecurityUtil {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                return jwtTokenProvider.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            return authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

