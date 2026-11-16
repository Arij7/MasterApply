package com.masterapply.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("role");
        roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(a -> a
                // Public
                .requestMatchers("/error").permitAll()
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/masters", "/api/masters/**").permitAll()

                // Administrateur uniquement
                .requestMatchers("/api/masters", "/api/masters/**").hasRole("ADMIN")
                .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/paiements", "/api/paiements/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,
                        "/api/candidatures/*/examen",
                        "/api/candidatures/*/decision").hasRole("ADMIN")

                // Candidat uniquement
                .requestMatchers(HttpMethod.POST,
                        "/api/candidatures",
                        "/api/candidatures/*/documents",
                        "/api/candidatures/*/paiements/simulation",
                        "/api/candidatures/*/inscription").hasRole("CANDIDAT")
                .requestMatchers(HttpMethod.PATCH,
                        "/api/candidatures/*/soumettre").hasRole("CANDIDAT")

                // Le reste : utilisateur connecté (consultation filtrée dans le service)
                .anyRequest().authenticated())
            .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }
}