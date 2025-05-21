package monostore.backend.routes;

import lombok.Getter;
import lombok.Setter;
import monostore.backend.datastore.DataStore;
import monostore.backend.models.Cart;
import monostore.backend.models.CartItem;
import monostore.backend.models.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    // Helper: Initialize cart if it doesn't exist
    private Cart initializeCart(String userId) {
        return DataStore.carts.computeIfAbsent(userId, k -> new Cart());
    }

    // Get cart
    //      @RequestHeader("user") String user


    @GetMapping
    public ResponseEntity<?> getCart(
        @RequestAttribute("user") String headers
        ) {
      System.out.println("Headers: " + headers);

//      System.out.println("HERE");
//        System.out.println("User IS: " + user);

//        Cart cart = initializeCart(userId);
      String user = "foo";
      return ResponseEntity.ok("User ID: " + user);
//        return ResponseEntity.ok(cart);
    }

    // Add item to cart
    @PostMapping("/items")
    public ResponseEntity<?> addItem(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody CartItemRequest request
    ) {
        Optional<Product> productOpt = DataStore.products.stream()
                .filter(p -> p.getId().equals(request.getProductId()))
                .findFirst();

        if (productOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found");
        }

        Product product = productOpt.get();

        if (request.getQuantity() > product.getStock()) {
            return ResponseEntity.badRequest().body("Not enough stock available");
        }

        Cart cart = initializeCart(userId);

        // Check if product already in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStock()) {
                return ResponseEntity.badRequest().body("Not enough stock available");
            }
            existingItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem(product, request.getQuantity());
            cart.getItems().add(newItem);
        }

        // Recalculate cart total
        cart.setTotal(cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum());

        return ResponseEntity.ok(cart);
    }

    // Update cart item
    @PutMapping("/items/{productId}")
    public ResponseEntity<?> updateItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String productId,
            @RequestBody CartItemRequest request
    ) {
        if (request.getQuantity() < 1) {
            return ResponseEntity.badRequest().body("Invalid quantity");
        }

        Cart cart = initializeCart(userId);
        Optional<CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Item not found in cart");
        }

        Optional<Product> productOpt = DataStore.products.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst();

        if (productOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found");
        }

        Product product = productOpt.get();

        if (request.getQuantity() > product.getStock()) {
            return ResponseEntity.badRequest().body("Not enough stock available");
        }

        CartItem item = itemOpt.get();
        item.setQuantity(request.getQuantity());

        cart.setTotal(cart.getItems().stream()
                .mapToDouble(i -> i.getQuantity() * i.getProduct().getPrice())
                .sum());

        return ResponseEntity.ok(cart);
    }

    // Remove item from cart
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<?> removeItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String productId
    ) {
        Cart cart = initializeCart(userId);
        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        if (!removed) {
            return ResponseEntity.status(404).body("Item not found in cart");
        }
        cart.setTotal(cart.getItems().stream()
                .mapToDouble(i -> i.getQuantity() * i.getProduct().getPrice())
                .sum());
        return ResponseEntity.ok(cart);
    }

    // Clear cart
    @DeleteMapping
    public ResponseEntity<?> clearCart(@RequestHeader("X-User-Id") String userId) {
        Cart cart = initializeCart(userId);
        cart.getItems().clear();
        cart.setTotal(0);
        return ResponseEntity.ok(cart);
    }

    // DTO for request body
    @Setter
    @Getter
    public static class CartItemRequest {
        private String productId;
        private int quantity;

    }
}
