package monostore.backend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import monostore.backend.models.Cart;
import monostore.backend.models.CartItem;
import monostore.backend.models.Product;

@Service
public class CartService {

  private final ProductService productService;
  // Simulating an in-memory datastore for carts
  private final Map<String, Cart> cartStore = new HashMap<>();

  public CartService(ProductService productService) {
    this.productService = productService;
  }

  private Cart initializeCart(String userId) {
    return cartStore.computeIfAbsent(userId, k -> new Cart());
  }

  public Cart getCartByUserId(String userId) {
    return initializeCart(userId);
  }

  public Cart addItemToCart(String userId, Product product, Integer quantity) {
    Cart cart = getCartByUserId(userId);
    CartItem cartItem = new CartItem(product, quantity);

    Optional<CartItem> existingItem = cart.getItems()
        .stream()
        .filter(item -> item.getProduct().getId().equals(product.getId()))
        .findFirst();

    if (existingItem.isPresent()) {
      existingItem.get().setQuantity(existingItem.get().getQuantity() +
          quantity);
    } else {
      cart.getItems().add(cartItem);
    }

    // Update the cart total
    cart.setTotal(
        cart.getItems()
            .stream()
            .mapToDouble(
                item -> item.getProduct().getPrice() * item.getQuantity())
            .sum());
    return cart;
  };

  public Cart clearCart(String userId) {
    cartStore.remove(userId);
    return getCartByUserId(userId);
  }

  public Cart updateItemInCart(String userId, String productId, int quantity) {
    Cart cart = cartStore.get(userId);

    if (quantity < 1) {
      throw new RuntimeException("Invalid quantity");
    }

    Optional<CartItem> itemOpt = cart.getItems()
        .stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst();

    if (itemOpt.isEmpty()) {
      throw new RuntimeException("Item not found in cart");
    }

    Product product = productService.getProductById(productId);
    if (quantity > product.getStock()) {
      throw new RuntimeException("Not enough stock available");
    }

    CartItem item = itemOpt.get();
    item.setQuantity(quantity);
    cart.setTotal(
        cart.getItems()
            .stream()
            .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
            .sum());

    return cart;
  }

  public Cart removeItemFromCart(String userId, String productId) {
    Cart cart = getCartByUserId(userId);
    if (cart == null) {
      throw new RuntimeException("Cart not found");
    }
    cart.getItems().removeIf(
        item -> item.getProduct().getId().equals(productId));

    cart.setTotal(
        cart.getItems()
            .stream()
            .mapToDouble(
                item -> item.getProduct().getPrice() * item.getQuantity())
            .sum());
    return cart;
  }
}
