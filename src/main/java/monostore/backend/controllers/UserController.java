package monostore.backend.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import monostore.backend.models.CustomUserDetails;
import monostore.backend.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final AuthenticationManager authenticationManager;
  private final SessionService sessionService;

  public UserController(AuthenticationManager authenticationManager,
                        SessionService sessionService) {
    this.authenticationManager = authenticationManager;
    this.sessionService = sessionService;
  }

  // Login user
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                 HttpServletRequest httpRequest) {
    String email = request.getEmail();
    String password = request.getPassword();

    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(email, password);

    try {
      Authentication auth = authenticationManager.authenticate(authToken);
      SecurityContextHolder.getContext().setAuthentication(auth);

      CustomUserDetails userDetails = (CustomUserDetails)auth.getPrincipal();

      // Create HTTP session
      HttpSession session = httpRequest.getSession(true);
      session.setAttribute(
          HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
          SecurityContextHolder.getContext());

      // Also store in our session service for additional control
      String sessionId = sessionService.createSession(userDetails);
      session.setAttribute("sessionId", sessionId);

      return ResponseEntity.ok(Map.of("message", "Login successful", "username",
                                      auth.getName(), "userId",
                                      userDetails.getId()));

    } catch (AuthenticationException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid username or password");
    }
  }

  // Logout user
  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest httpRequest) {
    HttpSession session = httpRequest.getSession(false);
    if (session != null) {
      String sessionId = (String)session.getAttribute("sessionId");
      if (sessionId != null) {
        sessionService.removeSession(sessionId);
      }
      session.invalidate();
    }

    SecurityContextHolder.clearContext();

    return ResponseEntity.ok(Map.of("message", "Logout successful"));
  }

  // Get user details
  @GetMapping("/profile")
  public ResponseEntity<?> getUserDetails() {
    Authentication auth =
        SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Unauthorized");
    }
    CustomUserDetails userDetails = (CustomUserDetails)auth.getPrincipal();
    return ResponseEntity.ok(Map.of("user", userDetails));
  }

  @Getter
  @Setter
  public static class LoginRequest {
    private String email;
    private String password;
  }
}
