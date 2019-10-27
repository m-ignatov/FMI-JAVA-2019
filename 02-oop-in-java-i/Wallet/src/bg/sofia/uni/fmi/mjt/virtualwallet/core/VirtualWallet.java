package bg.sofia.uni.fmi.mjt.virtualwallet.core;

import bg.sofia.uni.fmi.mjt.virtualwallet.core.card.Card;
import bg.sofia.uni.fmi.mjt.virtualwallet.core.payment.PaymentInfo;

import java.time.LocalDateTime;

public class VirtualWallet implements VirtualWalletAPI {

    private static final int CARD_LIST_MAX_SIZE = 5;
    private static final int TRANSACTION_LIST_MAX_SIZE = 10;

    private Card[] cardList;
    private Transaction[] transactions;
    private int cardListSize;
    private int transactionListSize;

    public VirtualWallet() {
        cardList = new Card[CARD_LIST_MAX_SIZE];
        cardListSize = 0;

        transactions = new Transaction[TRANSACTION_LIST_MAX_SIZE];
        transactionListSize = 0;
    }

    @Override
    public boolean registerCard(Card card) {
        if (cardListSize == 5) {
            return false;
        }
        if (!isValid(card) || contains(card)) {
            return false;
        }
        cardList[cardListSize++] = card;
        return true;
    }

    @Override
    public boolean executePayment(Card card, PaymentInfo paymentInfo) {
        if (!isValid(card) || !isValid(paymentInfo) || !contains(card)) {
            return false;
        }

        if (paymentInfo.getCost() > card.getAmount() || paymentInfo.getCost() < 0) {
            return false;
        }
        if (card.executePayment(-1 * paymentInfo.getCost())) {
            createTransaction(card, paymentInfo);
            return true;
        }
        return false;
    }

    private void createTransaction(Card card, PaymentInfo paymentInfo) {
        Transaction transaction = new Transaction(
                card.getName(),
                LocalDateTime.now(),
                paymentInfo
        );

        if (transactionListSize == TRANSACTION_LIST_MAX_SIZE) {
            transactionListSize = 0;
        }
        transactions[transactionListSize++] = transaction;
    }

    @Override
    public boolean feed(Card card, double amount) {
        if (amount < 0) {
            return false;
        }
        if (!isValid(card) || !contains(card)) {
            return false;
        }
        return card.executePayment(amount);
    }

    @Override
    public Card getCardByName(String name) {
        if (name == null) {
            return null;
        }
        for (int i = 0; i < cardListSize; i++) {
            if (name.equals(cardList[i].getName())) {
                return cardList[i];
            }
        }
        return null;
    }

    @Override
    public int getTotalNumberOfCards() {
        return cardListSize;
    }

    private boolean isValid(Card card) {
        return (card != null && card.getName() != null);
    }

    private boolean isValid(PaymentInfo paymentInfo) {
        return (paymentInfo != null
                && paymentInfo.getLocation() != null
                && paymentInfo.getReason() != null);
    }

    private boolean contains(Card card) {
        for (int i = 0; i < cardListSize; i++) {
            if (cardList[i].equals(card)) {
                return true;
            }
        }
        return false;
    }
}
