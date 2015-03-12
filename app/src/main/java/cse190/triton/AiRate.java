package cse190.triton;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class AiRate {
    double winRate;
    String aiName;
    Boolean fold;
    int currentBet;
    public AiRate(String hand, String name) {

        String base;
        if (checkCards(hand, "9s,2s")) {
            base = "9s,2s";
        }

        else if (checkCards(hand, "Kd,4h")) {
            base = "Kd,4h";
        }

        else {
            base = "Js,3s";
        }


        String pHands = (hand + "|" + base);

        Hand[] hands;
        Deck aiDeck = new Deck();
        int times = 1000;

        String[] sHands = pHands.split("\\|");
        int handCount = sHands.length;
        hands = new Hand[handCount];
        for(int i = 0; i < handCount; i++) {
            hands[i] = new Hand(sHands[i]);
            aiDeck.removeCards(hands[i].hCards);
        }
        aiDeck.enumRandom(5, times);

        int[] hValues = new int[handCount];
        int keyBoardSize = aiDeck.keysBoard.length;
        for(int i = 0; i < keyBoardSize; i++) {
            for(int j = 0; j < handCount; j++) {
                hValues[j] = Eva.getValue(aiDeck.keysBoard[i] * hands[j].hKey,
                        aiDeck.handsBoard[i] | hands[j].hCards);
            }

            int winningHand = 0;
            int winningValue = 0;
            for(int j = 0; j < handCount; j++) {
                if(hValues[j] >= winningValue) {
                    //check for tie
                    if(winningValue == hValues[j]) {
                        winningHand = -1;
                    } else {
                        winningValue = hValues[j];
                        winningHand = j;
                    }
                }
            }
            if(winningHand >= 0)
                hands[winningHand].wins();
        }

        double winPercentage = 0;
        winPercentage = hands[0].wins / (double)(aiDeck.keysBoard.length) * 100;
        winPercentage = (double) Math.round(winPercentage * 10) / 10;

        winRate = winPercentage;
        aiName = name;
        fold = false;
        currentBet = 0;
    }


    public boolean checkCards(String tempHand, String base){
        String[] cards = tempHand.split(",");
        String[] baseCards = base.split(",");
        if(cards[0].equals(baseCards[0]) || cards[1].equals(baseCards[0])) {
            return false;
        }

        else if(cards[0].equals(baseCards[1]) || cards[1].equals(baseCards[1])) {
            return false;
        }
        else {
            return true;
        }
    }

    public String whatToDo(double betAmount) {

        //bluffing 5% chance
        Random rand = new Random();
        int randomNumber = rand.nextInt(20);
        if(randomNumber == 1) {
            winRate = winRate + 20;
        }

        double checker = winRate - (betAmount/Settings.getHandStartingMoney() * 100);

        if (checker < -15) {
            return "FOLD";
        }

        else if (checker < 60) {
            return "CHECK";
        }

        else {
            return "RAISE";
        }
    }
    public void printRate() {
        System.out.println(winRate);
    }

}
