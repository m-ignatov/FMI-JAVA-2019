package bg.sofia.uni.fmi.mjt.shopping.portal;

import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.NoOfferFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.OfferAlreadySubmittedException;
import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.ProductNotFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.Offer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShoppingDirectoryImpl implements ShoppingDirectory {

    private static final int DAYS = 30;

    private Map<String, Set<Offer>> offers;
    private PriceStatisticDirectory priceStatisticDirectory;

    public ShoppingDirectoryImpl() {
        this.offers = new HashMap<>();
        this.priceStatisticDirectory = new PriceStatisticDirectory();
    }

    @Override
    public Collection<Offer> findAllOffers(String productName) throws ProductNotFoundException {
        validate(productName);
        LocalDate dateUntil = LocalDate.now().minusDays(DAYS);
        return sortByTotalPrice(findOffers(productName, dateUntil));
    }

    @Override
    public Offer findBestOffer(String productName) throws ProductNotFoundException, NoOfferFoundException {
        validate(productName);
        LocalDate dateUntil = LocalDate.now().minusDays(DAYS);

        List<Offer> offersByProductName = sortByTotalPrice(findOffers(productName, dateUntil));
        if (offersByProductName.isEmpty()) {
            throw new NoOfferFoundException();
        }
        return offersByProductName.get(0);
    }

    @Override
    public Collection<PriceStatistic> collectProductStatistics(String productName) throws ProductNotFoundException {
        validate(productName);
        return priceStatisticDirectory.collect(productName);
    }

    @Override
    public void submitOffer(Offer offer) throws OfferAlreadySubmittedException {
        validate(offer);

        String productName = offer.getProductName();
        Set<Offer> offerList = offers.get(productName);

        if (offerList == null) {
            offerList = new HashSet<>();
        } else if (offerList.contains(offer)) {
            throw new OfferAlreadySubmittedException();
        }
        offerList.add(offer);

        offers.put(productName, offerList);
        priceStatisticDirectory.add(offer, offer.getDate());
    }

    private void validate(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
    }

    private List<Offer> findOffers(String productName, LocalDate sinceDate) throws ProductNotFoundException {
        Set<Offer> offersByProductName = offers.get(productName);
        if (offersByProductName == null) {
            throw new ProductNotFoundException();
        }

        List<Offer> filteredOffers = new ArrayList<>();
        for (Offer offer : offersByProductName) {
            if (offer.getDate().isAfter(sinceDate)) {
                filteredOffers.add(offer);
            }
        }
        return filteredOffers;
    }

    private List<Offer> sortByTotalPrice(List<Offer> offers) {
        offers.sort(new Comparator<Offer>() {
            public int compare(Offer o1, Offer o2) {
                return Double.compare(o1.getTotalPrice(), o2.getTotalPrice());
            }
        });
        return offers;
    }
}
