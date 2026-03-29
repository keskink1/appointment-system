package com.keskin.common.util;

import com.keskin.common.enums.Role;
import com.keskin.common.exception.ForbiddenException;
import java.util.UUID;

public final class AuthorizationUtil {

    private AuthorizationUtil() {}

    /**
     * Checks if the current user has permission to access a specific user's resource.
     * ADMIN can access any record.
     * USER can only access their own record.
     */
    public static void checkUserAccess(UUID targetUserId, UUID currentUserId, Role currentUserRole) {
        if (currentUserRole == Role.ADMIN) {
            return;
        }

        if (!targetUserId.equals(currentUserId)) {
            throw new ForbiddenException("No authorization");
        }
    }

    /**
     * Validates if the current user has delete permissions.
     * Only ADMIN role is allowed to perform deletion.
     */
    public static void checkAdmin(Role currentUserRole) {
        if (currentUserRole != Role.ADMIN) {
            throw new ForbiddenException("Only admin can perform this action!");
        }
    }

    /**
     * Parses the role from the header string to the Role Enum.
     * Handles both "ROLE_ADMIN" and "ADMIN" formats.
     */
    public static Role parseRole(String roleHeader) {
        if (roleHeader == null || roleHeader.isBlank()) {
            throw new ForbiddenException("Role header is blank or null");
        }

        try {
            String roleName = roleHeader.startsWith("ROLE_")
                    ? roleHeader.substring(5)
                    : roleHeader;
            return Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException(" Invalid security role: " + roleHeader);
        }
    }
}