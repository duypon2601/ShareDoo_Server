package com.server.ShareDoo.config;


import com.server.ShareDoo.filter.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Value("${mathcha_edu.jwt.base64-secret}")
    private String jwtKey;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/login",
            "/api/auth/**",
            "/api/users/register",
            "/api/embeddings/**",
            "/api/suggestions/products",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/api-docs/**",
            "/api/hf/generate",
            "/api/hf/test",
            "/api/products/recommendations",
//            "/api/embeddings/minilm",
//            "/api/embeddings/multilingual",
//            "/api/embeddings/distiluse",
//            "/api/embeddings/similarity"
    };


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
//        http
//                .csrf(c -> c.disable())
//                .authorizeHttpRequests(
//                        authz -> authz
//                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
//                                .anyRequest().authenticated())
//                .oauth2ResourceServer((oauth2) -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()).jwtAuthenticationConverter(jwtAuthenticationConverter()))
//                        .authenticationEntryPoint(customAuthenticationEntryPoint)
//                )
//                .exceptionHandling(
//                        exceptions -> exceptions
//                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) // 401
//                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) // 403
//                .formLogin(f -> f.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        http.addFilterBefore(new Filter(PUBLIC_ENDPOINTS), AbstractPreAuthenticatedProcessingFilter.class);
//        return http.build();
//    }
@Bean
public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
    http
            .csrf(c -> c.disable())
            .authorizeHttpRequests(
                    authz -> authz
                            .requestMatchers(PUBLIC_ENDPOINTS).permitAll() // Cho phép truy cập công khai
                            .anyRequest().authenticated())
            .oauth2ResourceServer((oauth2) -> oauth2
                    .jwt(jwtConfigurer -> jwtConfigurer
                            .decoder(jwtDecoder())
                            .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
            )
            .exceptionHandling(
                    exceptions -> exceptions
                            .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                            .accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.addFilterBefore(new Filter(PUBLIC_ENDPOINTS), AbstractPreAuthenticatedProcessingFilter.class);
    return http.build();
}

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(jwtKey.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
    }



}

