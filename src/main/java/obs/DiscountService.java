package obs;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DiscountService {

    public static Function<Double, Function<Double, Double>> curryDiscount() {
        return rate -> price -> price * (1 - rate);
    }

    public static Function<Double, Function<Double, Function<Double, Double>>> curryMultipleDiscounts() {
        return baseDiscount -> additionalDiscount -> price -> 
            price * (1 - baseDiscount) * (1 - additionalDiscount);
    }

    public static Function<Double, Function<Double, Function<Double, Double>>> curryDiscountWithTax() {
        return discount -> taxRate -> basePrice -> {
            double afterDiscount = basePrice * (1 - discount);
            return afterDiscount * (1 + taxRate);
        };
    }

    public static final Function<Double, Double> DISCOUNT_10_PERCENT =
            curryDiscount().apply(0.10);

    public static final Function<Double, Double> DISCOUNT_20_PERCENT =
            curryDiscount().apply(0.20);

    public static final Function<Double, Double> DISCOUNT_30_PERCENT =
            curryDiscount().apply(0.30);

    public static final Function<Double, Double> DISCOUNT_50_PERCENT =
            curryDiscount().apply(0.50);

    public static Function<Double, Function<Item, Item>> curryItemDiscount() {
        return rate -> item -> {
            int discountedPrice = (int) (item.getPrice() * (1 - rate));
            return item.withPrice(discountedPrice);
        };
    }

    public static Function<Double, Function<Double, Function<Item, Item>>> curryMultipleItemDiscounts() {
        return baseDiscount -> additionalDiscount -> item -> {
            int afterFirst = (int) (item.getPrice() * (1 - baseDiscount));
            int afterSecond = (int) (afterFirst * (1 - additionalDiscount));
            return item.withPrice(afterSecond);
        };
    }

    public static final Function<Item, Item> ITEM_DISCOUNT_10 =
            curryItemDiscount().apply(0.10);

    public static final Function<Item, Item> ITEM_DISCOUNT_20 =
            curryItemDiscount().apply(0.20);

    public static final Function<Item, Item> ITEM_DISCOUNT_30 =
            curryItemDiscount().apply(0.30);

    public static final Function<Item, Item> ITEM_DISCOUNT_50 =
            curryItemDiscount().apply(0.50);

    public static Function<Double, Function<Integer, Function<Item, Item>>> curryConditionalDiscount() {
        return rate -> minQty -> item -> 
            item.getQty() >= minQty ? applyDiscount(item, rate) : item;
    }

    public static Function<Double, Function<Double, Function<Double, Function<Integer, Function<Double, Double>>>>>
    tieredDiscountCurried() {
        return tier1 -> tier2 -> tier3 -> quantity -> price -> {
            if (quantity >= 100) return price * (1 - tier3);
            if (quantity >= 50) return price * (1 - tier2);
            return price * (1 - tier1);
        };
    }

    public static Function<Double, Double> createDiscount(double rate) {
        return curryDiscount().apply(rate);
    }

    public static Function<Item, Item> createItemDiscount(double rate) {
        return curryItemDiscount().apply(rate);
    }

    public static Function<Integer, Function<Double, Double>> createTieredDiscount(
            double tier1Rate, double tier2Rate, double tier3Rate) {
        return tieredDiscountCurried().apply(tier1Rate)
                                      .apply(tier2Rate)
                                      .apply(tier3Rate);
    }

    private static Item applyDiscount(Item item, double rate) {
        int discountedPrice = (int) (item.getPrice() * (1 - rate));
        return item.withPrice(discountedPrice);
    }

    public static Function<Item, Item> composeDiscounts(
            Function<Item, Item> discount1,
            Function<Item, Item> discount2) {
        return item -> discount2.apply(discount1.apply(item));
    }

    public static List<Item> applyCurriedDiscount(List<Item> items, double rate) {
        Function<Item, Item> discountFunction = createItemDiscount(rate);
        return items.stream()
                   .map(discountFunction)
                   .collect(Collectors.toList());
    }

    public static List<Item> applyMultipleCurriedDiscounts(
            List<Item> items, double rate1, double rate2) {
        Function<Item, Item> discount1 = createItemDiscount(rate1);
        Function<Item, Item> discount2 = createItemDiscount(rate2);
        Function<Item, Item> combined = composeDiscounts(discount1, discount2);
        return items.stream()
                   .map(combined)
                   .collect(Collectors.toList());
    }

    public static List<Item> applyCurriedConditionalDiscount(
            List<Item> items, double rate, int minQuantity) {
        Function<Item, Item> discountFunction = 
            curryConditionalDiscount()
                .apply(rate)
                .apply(minQuantity);
        return items.stream()
                   .map(discountFunction)
                   .collect(Collectors.toList());
    }

    public static double applyTieredCurriedDiscount(
            int quantity, double price, 
            double tier1, double tier2, double tier3) {
        return createTieredDiscount(tier1, tier2, tier3)
                .apply(quantity)
                .apply(price);
    }
}
