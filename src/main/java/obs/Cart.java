package obs;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Cart {
    private final List<Item> items;

    private Cart(List<Item> items) {
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }

    public static Cart empty() {
        return new Cart(new ArrayList<>());
    }

    public static Cart of(Item... items) {
        return new Cart(Arrays.asList(items));
    }

    public static Cart copy(Cart original) {
        return new Cart(new ArrayList<>(original.items));
    }

    public List<Item> getItems() {
        return items;
    }

    public java.util.stream.Stream<Item> stream() {
        return items.stream();
    }

    public Optional<Item> findItemByIsbn(String isbn) {
        return items.stream()
            .filter(item -> item.getIsbn().equals(isbn))
            .findFirst();
    }

    public boolean containsIsbn(String isbn) {
        return items.stream().anyMatch(item -> item.getIsbn().equals(isbn));
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Cart addItem(Item item) {
        Objects.requireNonNull(item);
        
        return findItemByIsbn(item.getIsbn())
            .map(existing -> {
                Item updated = existing.addQuantity(item.getQty());
                return updateItemInternal(updated);
            })
            .orElseGet(() -> {
                List<Item> newItems = new ArrayList<>(items);
                newItems.add(item);
                return new Cart(newItems);
            });
    }

    public Cart removeItem(String isbn) {
        List<Item> filtered = items.stream()
            .filter(item -> !item.getIsbn().equals(isbn))
            .collect(Collectors.toList());
        
        return filtered.size() == items.size() ? this : new Cart(filtered);
    }

    public Cart updateQuantity(String isbn, int newQty) {
        return findItemByIsbn(isbn)
            .map(item -> item.withQuantity(newQty))
            .map(this::updateItemInternal)
            .orElse(this);
    }

    private Cart updateItemInternal(Item updatedItem) {
        List<Item> newItems = items.stream()
            .map(item -> item.getIsbn().equals(updatedItem.getIsbn()) 
                ? updatedItem : item)
            .collect(Collectors.toList());
        return new Cart(newItems);
    }

    public Cart clear() {
        return isEmpty() ? this : empty();
    }

    public long calculateTotal() {
        return items.stream()
            .mapToLong(Item::calculateTotal)
            .sum();
    }

    public int getTotalQuantity() {
        return items.stream()
            .mapToInt(Item::getQty)
            .sum();
    }

    public Optional<Item> getMostExpensive() {
        return items.stream()
            .max(Comparator.comparingInt(Item::getPrice));
    }

    public Optional<Item> getCheapest() {
        return items.stream()
            .min(Comparator.comparingInt(Item::getPrice));
    }

    public Cart filter(Predicate<Item> predicate) {
        List<Item> filtered = items.stream()
            .filter(predicate)
            .collect(Collectors.toList());
        return new Cart(filtered);
    }

    public Cart itemsAbovePrice(int minPrice) {
        return filter(item -> item.getPrice() >= minPrice);
    }

    public Cart itemsBelowPrice(int maxPrice) {
        return filter(item -> item.getPrice() <= maxPrice);
    }

    public Cart lowStockItems(int threshold) {
        return filter(item -> item.getQty() < threshold);
    }

    public Cart applyDiscountToAll(double discountPercent) {
        List<Item> discounted = items.stream()
            .map(item -> item.applyDiscount(discountPercent))
            .collect(Collectors.toList());
        return new Cart(discounted);
    }

    public Cart sortByPrice() {
        List<Item> sorted = items.stream()
            .sorted(Comparator.comparingInt(Item::getPrice))
            .collect(Collectors.toList());
        return new Cart(sorted);
    }

    public Cart sortByTitle() {
        List<Item> sorted = items.stream()
            .sorted(Comparator.comparing(Item::getTitle))
            .collect(Collectors.toList());
        return new Cart(sorted);
    }

    public Cart andThen(java.util.function.Function<Cart, Cart> operation) {
        Objects.requireNonNull(operation);
        return operation.apply(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart)) return false;
        Cart cart = (Cart) o;
        return Objects.equals(items, cart.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public String toString() {
        return String.format("Cart{items=%d, total=%d}", items.size(), calculateTotal());
    }
}
