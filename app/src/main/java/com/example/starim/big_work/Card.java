package com.example.starim.big_work;

/**
 * Created by starim on 2018/1/3.
 */

public class Card {
    private int cardNumber;
    private int imageID;

    public Card(int cardNumber,int imageID){
        this.cardNumber = cardNumber;
        this.imageID = imageID;
    }
    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
}
