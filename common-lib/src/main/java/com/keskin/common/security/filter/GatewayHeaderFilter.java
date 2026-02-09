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

import static com.keskin.common.constants.AppConstants.*;

public class GatewayHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.contains("h2-console") || path.contains("v3/api-docs") || path.contains("swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = request.getHeader(HEADER_USER_ID);
        String role = request.getHeader(HEADER_USER_ROLE);
        String email = request.getHeader(HEADER_USER_MAIL);

        UserPrincipalDto principal = new UserPrincipalDto(userId, email);

        if (userId != null && !userId.isBlank() && !userId.equals("null") &&
                role != null && !role.isBlank() && !role.equals("null")) {

            var auth = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}