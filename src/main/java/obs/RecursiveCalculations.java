package obs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class RecursiveCalculations {

    public static double sumPrices(List<Item> items) {
        if (items.isEmpty()) return 0;
        return items.get(0).getPrice() + sumPrices(items.subList(1, items.size()));
    }

    public static double sumCartValues(List<Item> items) {
        if (items.isEmpty()) return 0;
        Item first = items.get(0);
        double firstValue = (double) first.getPrice() * first.getQty();
        return firstValue + sumCartValues(items.subList(1, items.size()));
    }

    public static int findMaxPrice(List<Item> items) {
        if (items.size() == 1) return items.get(0).getPrice();
        int firstPrice = items.get(0).getPrice();
        int maxOfRest = findMaxPrice(items.subList(1, items.size()));
        return Math.max(firstPrice, maxOfRest);
    }

    public static int findMinPrice(List<Item> items) {
        if (items.size() == 1) return items.get(0).getPrice();
        int firstPrice = items.get(0).getPrice();
        int minOfRest = findMinPrice(items.subList(1, items.size()));
        return Math.min(firstPrice, minOfRest);
    }

    public static List<Item> filterByMinPrice(List<Item> items, int minPrice) {
        if (items.isEmpty()) return Collections.emptyList();
        
        Item first = items.get(0);
        List<Item> restFiltered = filterByMinPrice(items.subList(1, items.size()), minPrice);
        
        if (first.getPrice() >= minPrice) {
            ArrayList<Item> result = new ArrayList<>(restFiltered);
            result.add(0, first);
            return result;
        }
        return restFiltered;
    }

    public static List<Item> filterByMaxPrice(List<Item> items, int maxPrice) {
        if (items.isEmpty()) return Collections.emptyList();
        
        Item first = items.get(0);
        List<Item> restFiltered = filterByMaxPrice(items.subList(1, items.size()), maxPrice);
        
        if (first.getPrice() <= maxPrice) {
            ArrayList<Item> result = new ArrayList<>(restFiltered);
            result.add(0, first);
            return result;
        }
        return restFiltered;
    }

    public static List<Item> filterByPriceRange(List<Item> items, int minPrice, int maxPrice) {
        if (items.isEmpty()) return Collections.emptyList();
        
        Item first = items.get(0);
        List<Item> restFiltered = filterByPriceRange(items.subList(1, items.size()), minPrice, maxPrice);
        
        if (first.getPrice() >= minPrice && first.getPrice() <= maxPrice) {
            ArrayList<Item> result = new ArrayList<>(restFiltered);
            result.add(0, first);
            return result;
        }
        return restFiltered;
    }

    public static Optional<Item> findByIsbnRecursive(List<Item> items, String isbn) {
        if (items.isEmpty()) return Optional.empty();
        
        Item first = items.get(0);
        if (first.getIsbn().equals(isbn)) return Optional.of(first);
        
        return findByIsbnRecursive(items.subList(1, items.size()), isbn);
    }

    public static int countItems(List<Item> items) {
        if (items.isEmpty()) return 0;
        return 1 + countItems(items.subList(1, items.size()));
    }

    public static boolean anyItemAbovePrice(List<Item> items, int priceThreshold) {
        if (items.isEmpty()) return false;
        if (items.get(0).getPrice() > priceThreshold) return true;
        return anyItemAbovePrice(items.subList(1, items.size()), priceThreshold);
    }

    public static boolean allItemsAbovePrice(List<Item> items, int priceThreshold) {
        if (items.isEmpty()) return true;
        if (items.get(0).getPrice() <= priceThreshold) return false;
        return allItemsAbovePrice(items.subList(1, items.size()), priceThreshold);
    }

    public static List<Item> applyDiscountRecursive(List<Item> items, double discountRate) {
        if (items.isEmpty()) return Collections.emptyList();
        
        Item first = items.get(0);
        Item discountedFirst = first.applyDiscount(discountRate);
        List<Item> restDiscounted = applyDiscountRecursive(items.subList(1, items.size()), discountRate);
        
        ArrayList<Item> result = new ArrayList<>();
        result.add(discountedFirst);
        result.addAll(restDiscounted);
        return result;
    }

    public static List<Item> doubleQuantitiesRecursive(List<Item> items) {
        if (items.isEmpty()) return Collections.emptyList();
        
        Item first = items.get(0);
        Item doubled = first.withQuantity(first.getQty() * 2);
        List<Item> restDoubled = doubleQuantitiesRecursive(items.subList(1, items.size()));
        
        ArrayList<Item> result = new ArrayList<>();
        result.add(doubled);
        result.addAll(restDoubled);
        return result;
    }

    public static double sumPricesTailRecursive(List<Item> items) {
        return sumPricesHelper(items, 0);
    }

    private static double sumPricesHelper(List<Item> items, double accumulator) {
        if (items.isEmpty()) return accumulator;
        double newAccumulator = accumulator + items.get(0).getPrice();
        return sumPricesHelper(items.subList(1, items.size()), newAccumulator);
    }

    public static double averagePriceTailRecursive(List<Item> items) {
        if (items.isEmpty()) return 0;
        return sumPricesTailRecursive(items) / countItems(items);
    }

    public static String listItemsRecursive(List<Item> items) {
        return listItemsHelper(items, 1);
    }

    private static String listItemsHelper(List<Item> items, int index) {
        if (items.isEmpty()) return "";
        
        Item first = items.get(0);
        String current = String.format("%d. %s - $%d%n", index, first.getTitle(), first.getPrice());
        String rest = listItemsHelper(items.subList(1, items.size()), index + 1);
        
        return current + rest;
    }

    public static List<Item> findByTitlePatternRecursive(List<Item> items, String pattern) {
        if (items.isEmpty()) return Collections.emptyList();
        
        Item first = items.get(0);
        List<Item> restMatching = findByTitlePatternRecursive(items.subList(1, items.size()), pattern);
        
        if (first.getTitle().toLowerCase().contains(pattern.toLowerCase())) {
            ArrayList<Item> result = new ArrayList<>();
            result.add(first);
            result.addAll(restMatching);
            return result;
        }
        return restMatching;
    }

    public static List<Item> deepCopyRecursive(List<Item> items) {
        if (items.isEmpty()) return Collections.emptyList();
        
        Item first = Item.copy(items.get(0));
        List<Item> restCopy = deepCopyRecursive(items.subList(1, items.size()));
        
        ArrayList<Item> result = new ArrayList<>();
        result.add(first);
        result.addAll(restCopy);
        return result;
    }
}
