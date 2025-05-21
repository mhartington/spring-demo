package monostore.backend.models;

import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserDetails extends User {

  @Getter
  private final Long id; // Custom ID field

  public CustomUserDetails(
      Long id,
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.id = id;
  }
}
