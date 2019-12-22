package bg.sofia.uni.fmi.mjt.shopping.portal.offer;

import java.time.LocalDate;

public interface Offer {

    /**
     * Returns the name of the product for which
     * the offer is made.
     */
    String getProductName();

    /**
     * Returns the date of the offer.
     */
    LocalDate getDate();

    /**
     * Returns a short description of the offer.
     */
    String getDescription();

    /**
     * Returns the offer's price for the product.
     */
    double getPrice();

    /**
     * Returns the offer's shipping price for the product.
     */
    double getShippingPrice();

    /**
     * Returns the offer's total price (equal to the price plus shipping price).
     */
    double getTotalPrice();
}