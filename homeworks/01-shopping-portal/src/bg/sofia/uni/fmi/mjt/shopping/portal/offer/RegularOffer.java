package bg.sofia.uni.fmi.mjt.shopping.portal.offer;

import java.time.LocalDate;
import java.util.Objects;

public class RegularOffer implements Offer {

    private String productName;
    private LocalDate date;
    private String description;
    private double price;
    private double shippingPrice;

    public RegularOffer(String productName, LocalDate date, String description,
                        double price, double shippingPrice) {
        this.productName = productName;
        this.date = date;
        this.description = description;
        this.price = price;
        this.shippingPrice = shippingPrice;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public LocalDate getDate() {
        return date;
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
    public double getShippingPrice() {
        return shippingPrice;
    }

    @Override
    public double getTotalPrice() {
        return getPrice() + getShippingPrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Offer)) return false;
        Offer that = (Offer) o;
        return getProductName().equalsIgnoreCase(that.getProductName()) &&
                Double.compare(that.getTotalPrice(), getTotalPrice()) == 0 &&
                getDate().equals(that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductName().toLowerCase(), getDate(), getTotalPrice());
    }
}
