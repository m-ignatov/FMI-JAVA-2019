package bg.sofia.uni.fmi.mjt.shopping.portal;

import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.NoOfferFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.OfferAlreadySubmittedException;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.ProductNotFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.Offer;

import java.util.Collection;

public interface ShoppingDirectory {

    /**
     * Returns a collection of offers submitted in the last 30 days
     * for the product with name @productName sorted by total price
     * in ascending order.
     *
     * @throws ProductNotFoundException if there is no product with name @productName
     * @throws IllegalArgumentException if @productName is null
     */
    Collection<Offer> findAllOffers(String productName) throws ProductNotFoundException;

    /**
     * Returns the most favorable offer for the product with name @productName
     * submitted in the last 30 days - the one with lowest total price.
     *
     * @throws ProductNotFoundException if there is no product with name @productName
     * @throws NoOfferFoundException    if there is no offer submitted in the last 30
     *                                  days for the product with name @productName
     * @throws IllegalArgumentException if @productName is null
     */
    Offer findBestOffer(String productName) throws ProductNotFoundException, NoOfferFoundException;

    /**
     * Returns a collection of price statistics for the product with name @productName
     * sorted by date in descending order.
     *
     * @throws ProductNotFoundException if there is no product with name @productName
     * @throws IllegalArgumentException if @productName is null
     */
    Collection<PriceStatistic> collectProductStatistics(String productName) throws ProductNotFoundException;

    /**
     * Submits a new offer.
     *
     * @throws OfferAlreadySubmittedException if an identical @offer has already been submitted
     * @throws IllegalArgumentException       if @offer is null
     */
    void submitOffer(Offer offer) throws OfferAlreadySubmittedException;
}