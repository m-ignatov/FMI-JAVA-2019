package bg.sofia.uni.fmi.mjt.virtualwallet.core.card;

import java.util.Objects;

public abstract class Card {

    private String name;
    private double amount;

    public Card(String name) {
        this.name = name;
        this.amount = 0;
    }

    public abstract boolean executePayment(double cost);

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Card card = (Card) o;
        return Objects.equals(name, card.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount);
    }
}