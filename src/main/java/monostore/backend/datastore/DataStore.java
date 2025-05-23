package monostore.backend.datastore;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import monostore.backend.models.Cart;
import monostore.backend.models.CustomUserDetails;
import monostore.backend.models.Order;
import monostore.backend.models.Product;
import monostore.backend.models.User;
public class DataStore {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public static final Map<String, Cart> carts = new HashMap<>();
    public static final List<Order> orders = new ArrayList<>();
    public static final List<User> users = Arrays.asList(
            new User( 1L, "johndoe", "john@example.com", passwordEncoder.encode("password123"), "ROLE_USER", Instant.now())
        );
}
