package obs;

import java.math.BigDecimal;
import java.util.Objects;

public final class Item {
    private final String name;
    private final BigDecimal price;
    
    private Item(Builder builder) {
        this.name = builder.name;
        this.price = builder.price;
    }
    
    public String getName() {
        return name;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public static class Builder {
        private String name;
        private BigDecimal price;
        
        public Builder withName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }
        
        public Item build() {
            return new Item(this);
        }
    }
    
    public BigDecimal applyDiscount(BigDecimal discount) {
        return price.subtract(discount);
    }
    
    public Item transformPrice(BigDecimal newPrice) {
        return new Builder().withName(name).withPrice(newPrice).build();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) && Objects.equals(price, item.price);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}