package monostore.backend.routes;

import monostore.backend.models.*;
import monostore.backend.service.OrderService;
import monostore.backend.service.CartService;
import monostore.backend.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final ProductService productService;

    public OrderController(OrderService orderService, CartService cartService, ProductService productService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.productService = productService;
    }

    // Get all orders for the authenticated user
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestAttribute("user") Claims user) {
        String userId = user.get("id").toString();
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    // Get order by ID for the authenticated user
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@RequestAttribute("user") Claims user, @PathVariable String id) {
        String userId = user.get("id").toString();
        Optional<Order> order = orderService.getOrderByIdAndUserId(id, userId);

        if (order.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found");
        }

        return ResponseEntity.ok(order.get());
    }

    // Create a new order
    @PostMapping
    public ResponseEntity<?> createOrder(
      @RequestAttribute("user") Claims user,
      @RequestBody OrderRequest orderRequest
    ) {

        String userId = user.get("id").toString();
        Cart cart = cartService.getCartByUserId(userId);

        if (cart == null || cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Cannot create order with an empty cart");
        }

        // Create the order
        Order newOrder = orderService.createOrder(userId, cart, orderRequest);

        // Deduct stock from products
        for (CartItem item : cart.getItems()) {
            productService.updateProductStock(item.getProduct().getId(), -item.getQuantity());
        }
        // Clear the cart
        cartService.clearCart(userId);

        return ResponseEntity.status(201)
        .body(newOrder);
    }

    // Cancel an order (only if it is pending)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@RequestAttribute("user") Claims user, @PathVariable String id) {
        String userId = user.get("id").toString();
        Optional<Order> order = orderService.getOrderByIdAndUserId(id, userId);

        if (order.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found");
        }

        Order existingOrder = order.get();
        if (!"pending".equals(existingOrder.getStatus())) {
            return ResponseEntity.badRequest().body("Cannot cancel order that is not pending");
        }

        // Update order status to 'cancelled'
        existingOrder.setStatus("cancelled");
        existingOrder.setCancelledAt(Instant.now());
        orderService.saveOrder(existingOrder);

        // Restore product stock
        for (CartItem item : existingOrder.getItems()) {
            productService.updateProductStock(item.getProduct().getId(), item.getQuantity());
        }

        return ResponseEntity.ok(existingOrder);
    }
}
