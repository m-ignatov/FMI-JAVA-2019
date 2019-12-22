package bg.sofia.uni.fmi.mjt.shopping.portal.offer;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

public class PremiumOffer implements Offer {

    private static final double MIN_DISCOUNT = 0.0;
    private static final double MAX_DISCOUNT = 100.0;

    private String productName;
    private LocalDate date;
    private String description;
    private double price;
    private double shippingPrice;
    private double discount;

    public PremiumOffer(String productName, LocalDate date, String description,
                        double price, double shippingPrice, double discount) {
        if (!(discount >= MIN_DISCOUNT && discount <= MAX_DISCOUNT)) {
            throw new IllegalArgumentException();
        }

        this.productName = productName;
        this.date = date;
        this.description = description;
        this.price = price;
        this.shippingPrice = shippingPrice;
        this.discount = format(discount);
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
        return (getPrice() + getShippingPrice()) * ((MAX_DISCOUNT - getDiscount()) / MAX_DISCOUNT);
    }

    public double getDiscount() {
        return discount;
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

    private Double format(Double number) {
        return Double.valueOf(String.format(Locale.ROOT, "%.2f", number));
    }
}
