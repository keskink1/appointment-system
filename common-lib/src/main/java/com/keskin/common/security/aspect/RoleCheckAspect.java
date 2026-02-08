package com.keskin.common.security.aspect;

import com.keskin.common.enums.Role;
import com.keskin.common.exception.UnauthorizedException;
import com.keskin.common.security.annotation.RequiresAdmin;
import com.keskin.common.util.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import static com.keskin.common.constants.AppConstants.HEADER_USER_ROLE;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleCheckAspect {

    /**
     * keep the parameter for type safety and future updates where annotation might need a parameter. ( example, super admin etc.)
     * @param requiresAdmin
     */
    @Before("@annotation(requiresAdmin)")
    public void checkRole(RequiresAdmin requiresAdmin){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String headerRole = request.getHeader(HEADER_USER_ROLE);
        Role userRole = AuthorizationUtil.parseRole(headerRole);

        AuthorizationUtil.checkAdmin(userRole);
    }
}
