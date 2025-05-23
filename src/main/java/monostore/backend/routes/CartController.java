package monostore.backend.routes;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;
import monostore.backend.models.Cart;
import monostore.backend.models.Product;

import monostore.backend.service.CartService;
import monostore.backend.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

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
  public ResponseEntity<?> getCart(@RequestAttribute("user") Claims user) {
    String id = user.get("id").toString();
    Cart cart = cartService.getCartByUserId(id);
    return ResponseEntity.ok(Map.of("cart", cart));
  }

  // Add item to cart
  @PostMapping("/items")
  public ResponseEntity<?> addItem(
    @RequestAttribute("user") Claims user,
    @RequestBody CartItemRequest request) {

    String userId = user.get("id").toString();
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
    @RequestAttribute("user") Claims user,
    @PathVariable String productId,
    @RequestBody CartItemRequest request) {

    String userId = user.get("id").toString();

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
  public ResponseEntity<?> removeItem(
    @RequestAttribute("user") Claims user,
    @PathVariable String productId) {
    String userId = user.get("id").toString();

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
  public ResponseEntity<?> clearCart(@RequestAttribute("user") Claims user) {
    String userId = user.get("id").toString();
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
