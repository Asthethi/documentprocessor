package com.asthethi.docprocessor.filter;

import com.asthethi.docprocessor.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Read the Authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        // 2. if there is no Bearer token, skip the filter
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token (everything after "Bearer")
        final String token = authorizationHeader.substring(7);

        try {
            final String username = jwtService.extractUsername(token);

            // 4. if user is not already authenticated
            if(username == null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 5. validate the token
                if(jwtService.isTokenValid(token , username)) {
                    // 6. create an authentication object and set it in the context
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null , Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        }catch (Exception e) {

        }

        filterChain.doFilter(request, response);

    }
}
