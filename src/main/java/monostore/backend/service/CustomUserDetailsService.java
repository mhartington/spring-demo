package monostore.backend.service;

import monostore.backend.models.CustomUserDetails;
import monostore.backend.models.User;

import java.time.Instant;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  public static final List<User> users = List.of(
    new User(1L, "johndoe", "john@example.com", passwordEncoder.encode("password123"), "ROLE_USER", Instant.now())
  );

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Lookup user from your DataStore
    User user = users.stream()
      .filter(u -> u.getEmail().equalsIgnoreCase(email))
      .findFirst()
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return new CustomUserDetails(
      user.getId(),
      user.getEmail(),
      user.getPassword(),
      Stream.of(user.getRole())
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList())

    );
  }
}
