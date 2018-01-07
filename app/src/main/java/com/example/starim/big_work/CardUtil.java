package com.example.starim.big_work;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by starim on 2018/1/5.
 */

public class CardUtil {
    public static ArrayList<Card> getCardList(){
        ArrayList<Card> cardList = new ArrayList<>();
        Random get = new Random();
        for(int i = 0;i<10;i++){
            int cardType = get.nextInt(32);
            if(cardType<4){
                cardList.add(new Card(1, R.drawable.c1));
            }
            else if(cardType<8){
                cardList.add(new Card(2, R.drawable.c2));
            }
            else if(cardType<12){
                cardList.add(new Card(3, R.drawable.c3));
            }
            else if(cardType<16){
                cardList.add(new Card(4, R.drawable.c4));
            }
            else if(cardType<20){
                cardList.add(new Card(5, R.drawable.c5));
            }
            else if(cardType<24){
                cardList.add(new Card(6, R.drawable.c6));
            }
            else if(cardType<28){
                cardList.add(new Card(7, R.drawable.c7));
            }
            else if(cardType<30){
                cardList.add(new Card(8, R.drawable.c8));
            }
            else if(cardType<32){
                cardList.add(new Card(9, R.drawable.c9));
            }
        }
        return cardList;
    }
    public static Card getCard(){
        Random get = new Random();
        Card card = null;
        int cardType = get.nextInt(24);
        if(cardType<4){
            card = new Card(2, R.drawable.c2);
        }
        else if(cardType<8){
            card = new Card(3, R.drawable.c3);
        }
        else if(cardType<12){
            card = new Card(4, R.drawable.c4);
        }
        else if(cardType<16){
            card = new Card(5, R.drawable.c5);
        }
        else if(cardType<20){
            card = new Card(6, R.drawable.c6);
        }
        else if(cardType<24){
            card = new Card(7, R.drawable.c7);
        }
        return card;
    }
}
