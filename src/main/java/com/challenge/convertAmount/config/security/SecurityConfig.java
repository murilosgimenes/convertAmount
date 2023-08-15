package com.challenge.convertAmount.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${jwt.secret}")
  private String jwtSecret;
  @Value("${cors.allowedOrigins}")
  private List<String> allowedOrigins;
  @Value("${cors.allowedHeaders}")
  private List<String> allowedHeaders;
  @Value("${cors.allowedMethods}")
  private List<String> allowedMethods;
  @Value("${cors.pathCORS}")
  private String pathCORS;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorize -> authorize.requestMatchers(
                    "/v2/api-docs",
                    "/configuration/ui",
                    "/swagger-resources/**",
                    "/configuration/security",
                    "/swagger-ui.html",
                    "/",
                    "/webjars/**",
                    "/v1/health",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/actuator/**"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
        )
        .addFilterBefore(
            new JwtFilter(jwtSecret),
            UsernamePasswordAuthenticationFilter.class
        )
        .exceptionHandling(
            exceptionHandling -> exceptionHandling.authenticationEntryPoint(
                (request, response, e) -> {
                  var objectMapper = new ObjectMapper();

                  response.setStatus(HttpStatus.UNAUTHORIZED.value());
                  response.setContentType("application/json");
                  response.getWriter().write(
                      objectMapper.writeValueAsString(
                          AuthenticationErrorResponse.builder()
                              .userMessage("Unauthorized.")
                              .developerMessage("Without permission to perform this action.")
                              .errorCode(HttpStatus.UNAUTHORIZED.value())
                              .build()
                      ));
                })
        );

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(this.allowedOrigins);
    configuration.setAllowedHeaders(this.allowedHeaders);
    configuration.setAllowedMethods(this.allowedMethods);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(this.pathCORS, configuration);

    return source;
  }
}
