package ru.golovkov.myrestapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.golovkov.myrestapp.service.PersonDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Value("${app.base-url}")
    private String baseUrl;

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                baseUrl + "/public/hello",
                                "/css/**",
                                "/img/**",
                                //swagger
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                baseUrl + "/public/login",
                                baseUrl + "/public/registration"
                        ).permitAll()
                        .requestMatchers(baseUrl + "/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .userDetailsService(personDetailsService)
                .formLogin(formLogin -> formLogin
                        .loginProcessingUrl(baseUrl + "/public/login")
                        .defaultSuccessUrl(baseUrl + "/public/hello")
                        .failureUrl(baseUrl + "/public/login?error")
                )
                .logout(logout -> logout
                        .logoutUrl(baseUrl + "/logout")
                        .logoutSuccessUrl(baseUrl + "/public/hello")
                )
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .build();
    }
}
