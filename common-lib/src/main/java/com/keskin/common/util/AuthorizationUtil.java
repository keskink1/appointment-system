package com.keskin.common.util;

import com.keskin.common.enums.Role;
import com.keskin.common.exception.UnauthorizedException;
import java.util.UUID;

public final class AuthorizationUtil {

    private AuthorizationUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Checks if the current user has permission to access a specific user's resource.
     * ADMIN can access any record.
     * USER can only access their own record.
     */
    public static void checkUserAccess(UUID targetUserId, UUID currentUserId, Role currentUserRole) {
        // Admins and Managers have full access
        if (currentUserRole == Role.ADMIN) {
            return;
        }

        // Users can only access their own data
        if (!targetUserId.equals(currentUserId)) {
            throw new UnauthorizedException("Unauthorized Access: You can only access your own records.");
        }
    }

    /**
     * Validates if the current user has delete permissions.
     * Only ADMIN role is allowed to perform deletion.
     */
    public static void checkDeletePermission(Role currentUserRole) {
        if (currentUserRole != Role.ADMIN) {
            throw new UnauthorizedException("Unauthorized Action: Only ADMIN can perform delete operations.");
        }
    }

    /**
     * Parses the role from the header string to the Role Enum.
     * Handles both "ROLE_ADMIN" and "ADMIN" formats.
     */
    public static Role parseRole(String roleHeader) {
        if (roleHeader == null || roleHeader.isBlank()) {
            throw new UnauthorizedException("Missing security role header.");
        }

        try {
            String roleName = roleHeader.startsWith("ROLE_")
                    ? roleHeader.substring(5)
                    : roleHeader;
            return Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid security role: " + roleHeader);
        }
    }
}