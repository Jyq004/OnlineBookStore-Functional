package obs;

import java.util.*;
import java.util.stream.Collectors;

public final class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public User.Result<Order> createOrder(int userid, Cart cart) {
        if (cart.isEmpty()) {
            return User.Result.failure("Cannot create order from empty cart");
        }

        Order order = new Order(
            UUID.randomUUID().toString(),
            userid,
            System.currentTimeMillis(),
            cart.getItems(),
            cart.calculateTotal(),
            "pending"
        );

        return repository.save(order)
            .map(User.Result::success)
            .orElseGet(() -> User.Result.failure("Failed to save order"));
    }

    public Optional<Order> findOrderById(String orderId) {
        return repository.findById(orderId);
    }

    public List<Order> getUserOrders(int userid) {
        return repository.findByUserId(userid);
    }

    public List<Order> getPendingOrders(int userid) {
        return getUserOrders(userid).stream()
            .filter(order -> "pending".equals(order.getStatus()))
            .collect(Collectors.toList());
    }

    public User.Result<Order> cancelOrder(String orderId) {
        return findOrderById(orderId)
            .map(order -> new Order(
                order.getId(),
                order.getUserId(),
                order.getCreatedAt(),
                order.getItems(),
                order.getTotalAmount(),
                "cancelled"
            ))
            .flatMap(cancelledOrder -> repository.update(cancelledOrder)
                .map(User.Result::success)
                .orElseGet(() -> User.Result.failure("Failed to cancel order")))
            .orElseGet(() -> User.Result.failure("Order not found"));
    }

    public OrderStats getOrderStats(int userid) {
        List<Order> orders = getUserOrders(userid);
        return new OrderStats(
            orders.size(),
            orders.stream().mapToLong(Order::getTotalAmount).sum(),
            orders.stream().mapToLong(Order::getTotalAmount).average().orElse(0)
        );
    }

    public static final class OrderStats {
        public final int totalOrders;
        public final long totalSpent;
        public final double averageOrderValue;

        public OrderStats(int totalOrders, long totalSpent, double averageOrderValue) {
            this.totalOrders = totalOrders;
            this.totalSpent = totalSpent;
            this.averageOrderValue = averageOrderValue;
        }

        @Override
        public String toString() {
            return String.format("OrderStats{total=%d, spent=%d, avg=%.2f}",
                totalOrders, totalSpent, averageOrderValue);
        }
    }
}
