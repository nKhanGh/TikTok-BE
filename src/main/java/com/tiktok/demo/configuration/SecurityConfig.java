package com.tiktok.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal=true)
public class SecurityConfig {
    CustomJwtDecoder jwtDecoder;
    CustomAccessDeniedHandler customAccessDeniedHandler;

    static String[] publicEndPointPost = {
        "/users", "/auth/login", "/auth/introspect", "/auth/logout", "/auth/refreshToken", 
        "/emails/sender/verifyCode", "emails/verify",
        "/auth/register", "/auth/verify-email"
    };

    static String[] publicEndpointGet = {
        "/users/exist/**", "/images/**", "/videos/public/**", "/comments/**"
    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http.authorizeHttpRequests(request -> request
            .requestMatchers(HttpMethod.POST, publicEndPointPost).permitAll()
            .requestMatchers(HttpMethod.GET, publicEndpointGet).permitAll()
            .anyRequest().authenticated()
        );

        http.oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwtCustomizer -> jwtCustomizer
                .decoder(jwtDecoder)
                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
            .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

        http.exceptionHandling(exceptionHandlingCustomizer -> exceptionHandlingCustomizer
            .accessDeniedHandler(customAccessDeniedHandler));

        http.csrf(AbstractHttpConfigurer::disable);


        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
