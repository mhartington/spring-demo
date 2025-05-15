package monostore.models;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class Order {
    private String id;
    private String userId;
    private List<CartItem> items;
    private double total;
    private String shippingAddress;
    private String paymentMethod;
    private String status; // e.g. "pending","completed","cancelled"
    private Instant createdAt;
    private Instant cancelledAt;
}
