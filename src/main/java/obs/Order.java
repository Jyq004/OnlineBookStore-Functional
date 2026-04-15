package obs;

import java.util.*;

public final class Order {
    private final String id;
    private final int userId;
    private final long createdAt;
    private final List<Item> items;
    private final long totalAmount;
    private final String status;

    public Order(String id, int userId, long createdAt, List<Item> items,
                 long totalAmount, String status) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getId() { return id; }
    public int getUserId() { return userId; }
    public long getCreatedAt() { return createdAt; }
    public List<Item> getItems() { return items; }
    public long getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("Order{id='%s', userId=%d, total=%d, status='%s'}",
            id, userId, totalAmount, status);
    }
}
