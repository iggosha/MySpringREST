package ru.golovkov.myrestapp.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.golovkov.myrestapp.exception.httpcommon.BadRequestException;
import ru.golovkov.myrestapp.exception.ExceptionDetails;
import ru.golovkov.myrestapp.service.PersonDetailsService;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final PersonDetailsService personDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String bearerPrefix = "Bearer ";

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith(bearerPrefix)) {
            String jwt = authHeader.substring(bearerPrefix.length());
            if (jwt.isBlank()) {
                setBadRequestInvalidJwtResponse(response, new BadRequestException("Empty or invalid JWT"));
                return;
            } else {
                try {
                    String username = jwtUtil.verifyTokenAndGetUsernameClaim(jwt);
                    UserDetails userDetails = personDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails,
                                    userDetails.getPassword(),
                                    userDetails.getAuthorities());

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (JWTVerificationException e) {
                    setBadRequestInvalidJwtResponse(response, e);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setBadRequestInvalidJwtResponse(HttpServletResponse response, RuntimeException e) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ExceptionDetails(e.toString()));
    }
}
