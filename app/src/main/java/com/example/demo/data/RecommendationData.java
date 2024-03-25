package com.example.demo.data;

public class RecommendationData {
    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public ImageData getImageData() {
        return imageData;
    }

    public void setImageData(ImageData imageData) {
        this.imageData = imageData;
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public void setLandmark(Landmark landmark) {
        this.landmark = landmark;
    }

    private ImageData imageData;
    private Landmark landmark;

    public RecommendationData(Card card,ImageData imageData,Landmark landmark){
        this.card = card ;
        this.imageData = imageData ;
        this.landmark = landmark ;

    }

}
