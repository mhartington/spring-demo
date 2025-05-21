package monostore.backend.config;

import jakarta.servlet.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;


@Component
public class AuthMiddleware implements Filter {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String USER_ATTRIBUTE = "user";
  private static final String MISSING_TOKEN_MESSAGE = "authorization header is missing or invalid";
  private static final String INVALID_TOKEN_MESSAGE = "Invalid token";

  private final JwtUtil jwtUtil;

  // Constructor-based dependency injection
  public AuthMiddleware(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  public void doFilter(
      ServletRequest request,
      ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    HttpServletResponse httpResponse = (HttpServletResponse) response;
    HttpServletRequest httpRequest = (HttpServletRequest) request;

    // String token = extractToken(httpRequest);

    try {
      String token = extractToken(httpRequest);
      
      if (token == null) {
        sendUnauthorizedError(httpResponse, MISSING_TOKEN_MESSAGE);
        return;
      }

      Jws<Claims> userClaims = jwtUtil.validateToken(token);

      if (userClaims != null) {
        System.out.println("User claims: " + userClaims.getBody());
        httpRequest.setAttribute(USER_ATTRIBUTE, userClaims.getBody().toString());
        chain.doFilter(httpRequest, httpResponse);
      }
    } catch (JwtException e) {
      sendUnauthorizedError(httpResponse, INVALID_TOKEN_MESSAGE);
    }
  }

  private String extractToken(HttpServletRequest request) {
    String authHeader = request.getHeader(AUTHORIZATION_HEADER);
    if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
      return authHeader.substring(BEARER_PREFIX.length());
    }
    return null;
  }

  private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
  }

}
