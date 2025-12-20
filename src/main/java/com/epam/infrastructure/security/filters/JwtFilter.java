package com.epam.infrastructure.security.filters;

import com.epam.infrastructure.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = getBearerToken(request.getHeader("Authorization"));

        if (token != null
                && jwtService.isTokenValid(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

             String username = jwtService.extractUsername(token);
             UserDetails userDetails = userDetailsService.loadUserByUsername(username);

             UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                     userDetails, null, userDetails.getAuthorities());
             SecurityContext context = SecurityContextHolder.createEmptyContext();
             context.setAuthentication(authToken);
             SecurityContextHolder.setContext(context);
        }
        filterChain.doFilter(request, response);
    }

    private String getBearerToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.replace("Bearer ", "").trim();
        }
        return null;
    }
}
