package monostore.backend.service;

import monostore.backend.datastore.DataStore;
import monostore.backend.models.CustomUserDetails;
import monostore.backend.models.User;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Lookup user from your DataStore
    User user = DataStore.users.stream()
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