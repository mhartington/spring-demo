package monostore.backend.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;
import monostore.backend.config.JwtUtil;
import monostore.backend.models.CustomUserDetails;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  public UserController(AuthenticationManager authenticationManager,
                        JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
  }

  // Login user
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    String email = request.getEmail();
    String password = request.getPassword();

    UsernamePasswordAuthenticationToken authdUser =
        new UsernamePasswordAuthenticationToken(email, password);

    try {
      Authentication auth = authenticationManager.authenticate(authdUser);

      SecurityContextHolder.getContext().setAuthentication(auth);
      CustomUserDetails userDetails = (CustomUserDetails)auth.getPrincipal();

      // Generate JWT token
      String token =
          jwtUtil.generateToken(email,
                                auth.getAuthorities()
                                    .stream()
                                    .findFirst()
                                    .map(GrantedAuthority::getAuthority)
                                    .orElse("USER"),
                                userDetails.getId());

      return ResponseEntity.ok(Map.of("message", "Login successful", "username",
                                      auth.getName(), "token", token));

    } catch (AuthenticationException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid username or password");
    }
  }

  @Getter
  @Setter
  public static class LoginRequest {
    private String email;
    private String password;
  }
}
