package bg.sofia.uni.fmi.mjt.shopping.item;

import java.util.Objects;

public class Chocolate implements Item {

    private String name;
    private String description;
    private double price;

    public Chocolate(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chocolate chocolate = (Chocolate) o;
        return Objects.equals(name, chocolate.name) &&
                Objects.equals(description, chocolate.description) &&
                Double.compare(chocolate.price, price) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price);
    }
}
