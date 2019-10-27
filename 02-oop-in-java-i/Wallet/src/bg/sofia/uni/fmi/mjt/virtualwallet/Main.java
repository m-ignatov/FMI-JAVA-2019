package bg.sofia.uni.fmi.mjt.virtualwallet;

import bg.sofia.uni.fmi.mjt.virtualwallet.core.VirtualWallet;
import bg.sofia.uni.fmi.mjt.virtualwallet.core.card.Card;
import bg.sofia.uni.fmi.mjt.virtualwallet.core.card.GoldenCard;
import bg.sofia.uni.fmi.mjt.virtualwallet.core.payment.PaymentInfo;

public class Main {

    public static void main(String... args) {
        VirtualWallet virtualWallet = new VirtualWallet();
        Card goldenCard = new GoldenCard("Visa");
        PaymentInfo paymentInfo = new PaymentInfo("Happy", "Sofia", 12.00);

        virtualWallet.registerCard(goldenCard);
        virtualWallet.feed(goldenCard, 200);
        virtualWallet.executePayment(goldenCard, paymentInfo);
    }
}
