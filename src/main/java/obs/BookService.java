package obs;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Predicate;

public final class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public List<Item> getAllBooks() {
        return repository.findAll();
    }

    public List<Item> searchBooks(Predicate<Item> criteria) {
        return repository.findAll().stream()
            .filter(criteria)
            .collect(Collectors.toList());
    }

    public Optional<Item> findByIsbn(String isbn) {
        return repository.findByIsbn(isbn);
    }

    public List<Item> findByCategory(String category) {
        return repository.findByCategory(category);
    }

    public List<Item> findByPriceRange(int minPrice, int maxPrice) {
        return repository.findAll().stream()
            .filter(item -> item.getPrice() >= minPrice && item.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    }

    public List<Item> findByTitleContains(String keyword) {
        return repository.findAll().stream()
            .filter(item -> item.getTitle().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
    }

    public List<Item> getBestsellers(int limit) {
        return repository.findAll().stream()
            .sorted(Comparator.comparingInt(Item::getQty).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<Item> getTopPriced(int limit) {
        return repository.findAll().stream()
            .sorted(Comparator.comparingInt(Item::getPrice).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<Item> applyBulkDiscount(List<Item> items, double discountPercent) {
        return items.stream()
            .map(item -> item.applyDiscount(discountPercent))
            .collect(Collectors.toList());
    }
}
