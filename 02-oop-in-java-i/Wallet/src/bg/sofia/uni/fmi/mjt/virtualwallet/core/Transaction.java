package bg.sofia.uni.fmi.mjt.virtualwallet.core;

import bg.sofia.uni.fmi.mjt.virtualwallet.core.payment.PaymentInfo;

import java.time.LocalDateTime;

public class Transaction {

    private String cardName;

    private LocalDateTime timestamp;

    private PaymentInfo paymentInfo;

    public Transaction(String cardName, LocalDateTime timestamp, PaymentInfo paymentInfo) {
        this.cardName = cardName;
        this.timestamp = timestamp;
        this.paymentInfo = paymentInfo;
    }

    public String getCardName() {
        return cardName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }
}
