package bg.sofia.uni.fmi.mjt.virtualwallet.core.card;

public class GoldenCard extends Card {

    public GoldenCard(String name) {
        super(name);
    }

    @Override
    public boolean executePayment(double cost) {
        setAmount(getAmount() + cost);
        if (cost < 0) {
            setAmount(getAmount() - cost * 0.15);
        }
        return true;
    }
}
