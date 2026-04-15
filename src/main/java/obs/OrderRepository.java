package obs;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(String id);
    List<Order> findByUserId(int userid);
    Optional<Order> save(Order order);
    Optional<Order> update(Order order);
    boolean delete(String id);
}
