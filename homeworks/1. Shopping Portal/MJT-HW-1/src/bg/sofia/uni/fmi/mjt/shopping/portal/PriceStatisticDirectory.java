package bg.sofia.uni.fmi.mjt.shopping.portal;

import bg.sofia.uni.fmi.mjt.shopping.portal.exceptions.ProductNotFoundException;
import bg.sofia.uni.fmi.mjt.shopping.portal.offer.Offer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PriceStatisticDirectory {

    private Map<String, Map<LocalDate, PriceStatistic>> statistics;

    PriceStatisticDirectory() {
        statistics = new HashMap<>();
    }

    Collection<PriceStatistic> collect(String productName) throws ProductNotFoundException {
        Map<LocalDate, PriceStatistic> priceStatistics = statistics.get(productName);
        if (priceStatistics == null) {
            throw new ProductNotFoundException();
        }

        List<PriceStatistic> values = new ArrayList<>(priceStatistics.values());
        values.sort(new Comparator<PriceStatistic>() {
            @Override
            public int compare(PriceStatistic o1, PriceStatistic o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        return values;
    }

    void add(Offer offer, LocalDate date) {
        Map<LocalDate, PriceStatistic> priceStatistics = statistics.get(offer.getProductName());

        if (priceStatistics == null) {
            priceStatistics = new HashMap<>();
        }

        double offerTotalPrice = offer.getTotalPrice();
        PriceStatistic priceStatisticOld = priceStatistics.get(date);
        PriceStatistic priceStatisticNew;

        if (priceStatisticOld == null) {
            priceStatisticNew = new PriceStatistic(date, offerTotalPrice, offerTotalPrice);
        } else {
            priceStatisticNew = new PriceStatistic(
                    date,
                    Math.min(offerTotalPrice, priceStatisticOld.getLowestPrice()),
                    calculateAverage(
                            priceStatisticOld.getAveragePrice(),
                            offerTotalPrice,
                            priceStatisticOld.getUpdatedCounter()));
        }
        priceStatistics.put(date, priceStatisticNew);
        statistics.put(offer.getProductName(), priceStatistics);
    }

    private double calculateAverage(double averagePrice, double price, int size) {
        return averagePrice + ((price - averagePrice) / (size + 1));
    }
}
