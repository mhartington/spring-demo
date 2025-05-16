package monostore.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private String image;
    private String category;
    private int stock;
    private Instant created_at;
    private Instant updated_at;

    public String getId() { return id; }
    public String getName() { return name; }
}
