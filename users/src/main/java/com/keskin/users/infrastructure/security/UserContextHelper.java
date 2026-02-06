package com.keskin.users.infrastructure.security;

import com.keskin.common.dto.UserPrincipalDto;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContextHelper {
    public static String getCurrentUserEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipalDto principal) {
            return principal.email();
        }
        return "SYSTEM";
    }

    public static String getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipalDto principal) {
            return principal.userId();
        }
        return null;
    }
}