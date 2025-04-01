package com.example.nvt.configuration;

import com.example.nvt.model.SuperAdmin;
import com.example.nvt.model.User;
import com.example.nvt.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver exceptionResolver;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String jwt = extractJwtFromCookies(request);
        String userEmail;

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            userEmail = jwtService.extractUsername(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // ✅ RESTORING SUPERADMIN LOGIN CHECK
                    if (userDetails.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals("SUPERADMIN"))) {

                        var superadmin = (SuperAdmin) userDetails;
                        boolean isFirstLogin = superadmin.isFirstLogin();

                        if (isFirstLogin && !request.getRequestURI().equals("/api/v1/auth/change-superadmin-password")) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Access Denied: You must change your password first.");
                            return;
                        }
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            exceptionResolver.resolveException(request, response, null, e);
        }
    }

    // Helper method to extract JWT from cookies
    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}