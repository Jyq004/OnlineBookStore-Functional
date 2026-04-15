package obs;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    List<Item> findAll();
    Optional<Item> findByIsbn(String isbn);
    List<Item> findByCategory(String category);
    Optional<Item> save(Item item);
    boolean delete(String isbn);
}
