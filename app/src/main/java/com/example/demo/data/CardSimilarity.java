package com.example.demo.data;

public class CardSimilarity {
    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Float similarity) {
        this.similarity = similarity;
    }

    private Card card;
    private Float similarity;

    public CardSimilarity(Card card, Float similarity) {
        this.card = card;
        this.similarity = similarity;
    }

}
