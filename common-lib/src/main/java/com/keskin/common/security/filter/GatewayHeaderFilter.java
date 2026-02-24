package com.keskin.common.security.filter;

import com.keskin.common.dto.UserPrincipalDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.keskin.common.constants.AppConstants.*;

public class GatewayHeaderFilter extends OncePerRequestFilter {

    private boolean isValidHeader(String value) {
        return value != null && !value.isBlank() && !value.equals("null");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String userIdStr = request.getHeader(HEADER_USER_ID);
        String role = request.getHeader(HEADER_USER_ROLE);
        String email = request.getHeader(HEADER_USER_MAIL);

        if (isValidHeader(userIdStr) && isValidHeader(role)) {
            try {
                UUID userId = UUID.fromString(userIdStr);

                UserPrincipalDto principal = new UserPrincipalDto(userId, email);

                var auth = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (IllegalArgumentException e) {
                logger.error("Invalid UUID format received from Gateway: " + userIdStr);
            }
        }

        filterChain.doFilter(request, response);
    }

}