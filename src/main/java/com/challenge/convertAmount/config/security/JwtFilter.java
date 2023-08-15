package com.challenge.convertAmount.config.security;

import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;

import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtFilter extends GenericFilter {

  @Serial
  private static final long serialVersionUID = -5380058288486988331L;

  private final String secretKey;

  public JwtFilter(String secret) {
    this.secretKey = secret;
  }

  @Override
  public void doFilter(ServletRequest servletRequest,
                       ServletResponse servletResponse,
                       FilterChain filterChain) throws IOException, ServletException {
    var request = (HttpServletRequest) servletRequest;
    var response = (HttpServletResponse) servletResponse;

    String authorization = this.getAuthorizationHeader(request);

    if (this.isValidToken(authorization)) {
      SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(
              authorization,
              null,
              new ArrayList<>()
          ));
    }

    filterChain.doFilter(request, response);
  }

  private String getAuthorizationHeader(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");

    if (authorization != null && !authorization.trim().isEmpty()) {
      return authorization.replace("Bearer ", "");
    }

    return "";
  }

  private boolean isValidToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(secretKey.getBytes())
          .build()
          .parseClaimsJws(token);

      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
