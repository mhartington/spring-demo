package monostore.backend.service;

import monostore.backend.models.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

  // Simulating an in-memory datastore for orders
  private final List<Order> orderStore = new ArrayList<>();

  /**
   * Retrieves all orders for a specific user.
   *
   * @param userId The ID of the user.
   * @return A list of orders for the user.
   */
  public List<Order> getOrdersByUserId(String userId) {
    return orderStore.stream()
      .filter(order -> order.getUserId().equals(userId))
      .collect(Collectors.toList());
  }

  /**
   * Retrieves an order by its ID if it belongs to a specific user.
   *
   * @param orderId The ID of the order.
   * @param userId  The ID of the user.
   * @return An Optional containing the found order or empty if not found.
   */
  public Optional<Order> getOrderByIdAndUserId(String orderId, String userId) {
    return orderStore.stream()
      .filter(order -> order.getId().equals(orderId) && order.getUserId().equals(userId))
      .findFirst();
  }

  /**
   * Creates a new order for the user based on their cart and order details.
   *
   * @param userId      The ID of the user.
   * @param cart        The cart details.
   * @param orderRequest The order request with shipping and payment information.
   * @return The created Order.
   */
  public Order createOrder(String userId, Cart cart, OrderRequest orderRequest) {

    System.out.println("Order Request: " + orderRequest);


    Order newOrder = new Order();
    newOrder.setId(generateOrderId());
    newOrder.setUserId(userId);
    newOrder.setItems(cart.getItems());
    newOrder.setTotal(cart.getTotal());
    newOrder.setShippingAddress(String.valueOf(orderRequest.getShippingAddress()));
    newOrder.setPaymentMethod(orderRequest.getPaymentMethod());
    newOrder.setStatus("pending");
    newOrder.setCreatedAt(Instant.now());

    orderStore.add(newOrder);
    return newOrder;
  }

  /**
   * Saves or updates an order in the order store.
   *
   * @param order The order to save.
   */
  public void saveOrder(Order order) {
    orderStore.removeIf(existingOrder -> existingOrder.getId().equals(order.getId()));
    orderStore.add(order);
  }

  /**
   * Generates a unique order ID.
   *
   * @return A unique string ID.
   */
  private String generateOrderId() {
    return "ORDER-" + Instant.now().toEpochMilli();
  }
}
