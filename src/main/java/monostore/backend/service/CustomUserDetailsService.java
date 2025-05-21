package monostore.backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

@Service("CustomUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

  private final UserDetailsService inMemoryUserDetailsManager;

  public CustomUserDetailsService(UserDetailsService inMemoryUserDetailsManager) {
    this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return inMemoryUserDetailsManager.loadUserByUsername(username);
  }
}

