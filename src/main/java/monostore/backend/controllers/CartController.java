package monostore.backend.controllers;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;
import monostore.backend.models.Cart;
import monostore.backend.models.CustomUserDetails;
import monostore.backend.models.Product;
import monostore.backend.service.CartService;
import monostore.backend.service.ProductService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

  private final CartService cartService;
  private final ProductService productService;

  public CartController(CartService cartService, ProductService productService) {
    this.cartService = cartService;
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<?> getCart() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String userId = userDetails.getId().toString();
    
    Cart cart = cartService.getCartByUserId(userId);
    return ResponseEntity.ok(Map.of("cart", cart));
  }

  // Add item to cart
  @PostMapping("/items")
  public ResponseEntity<?> addItem(@RequestBody CartItemRequest request) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String userId = userDetails.getId().toString();
    
    Product product = productService.getProductById(request.productId);

    if (request.getQuantity() > product.getStock()) {
      return ResponseEntity.badRequest().body("Not enough stock available");
    }
    Cart cart = cartService.addItemToCart(userId, product, request.getQuantity());

    return ResponseEntity.ok(Map.of(
      "cart", cart,
      "message", "Item added to cart"));

  }

  // Update cart item
  @PutMapping("/items/{productId}")
  public ResponseEntity<?> updateItem(
    @PathVariable String productId,
    @RequestBody CartItemRequest request) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String userId = userDetails.getId().toString();

    try {
      Cart cart = cartService.updateItemInCart(userId, productId, request.quantity);

      return ResponseEntity.ok(Map.of(
        "cart", cart,
        "message", "Cart Item Updated"));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }

  }

  @DeleteMapping("/items/{productId}")
  public ResponseEntity<?> removeItem(@PathVariable String productId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String userId = userDetails.getId().toString();

    try {
      Cart cart = cartService.removeItemFromCart(userId, productId);
      return ResponseEntity.ok(Map.of(
        "cart", cart,
        "message", "Item removed from the cart"));

    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }

  }

  // // Clear cart
  @DeleteMapping
  public ResponseEntity<?> clearCart() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String userId = userDetails.getId().toString();
    
    Cart cart = cartService.clearCart(userId);
    return ResponseEntity.ok(Map.of(
      "message", "Cart cleared successfully",
      "cart", cart));
  }

  // DTO for request body
  @Setter
  @Getter
  public static class CartItemRequest {
    @JsonProperty(required = true)
    private String productId;
    @JsonProperty(required = true)
    private int quantity;
  }
}
