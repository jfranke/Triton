package cse190.triton;

public class Grader {
    public String grade;

    public Grader(String hand) {
        String base;
        if (checkCards(hand, "Kd,4h")) {
            base = "Kd,4h";
        }

        else if (checkCards(hand, "9s,2s")) {
            base = "9s,2s";
        }

        else {
            base = "Js,3s";
        }


        String pHands = (hand + "|" + base);

        Hand[] hands;
        Deck gradeDeck = new Deck();
        int times = 10000;

        String[] sHands = pHands.split("\\|");
        int handCount = sHands.length;
        hands = new Hand[handCount];
        for(int i = 0; i < handCount; i++) {
            hands[i] = new Hand(sHands[i]);
            gradeDeck.removeCards(hands[i].hCards);
        }
        gradeDeck.enumRandom(5, times);

        int[] hValues = new int[handCount];
        int keyBoardSize = gradeDeck.keysBoard.length;
        for(int i = 0; i < keyBoardSize; i++) {
            for(int j = 0; j < handCount; j++) {
                hValues[j] = Eva.getValue(gradeDeck.keysBoard[i] * hands[j].hKey,
                        gradeDeck.handsBoard[i] | hands[j].hCards);
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

        double winPercentage;
        winPercentage = hands[0].wins / (double)(gradeDeck.keysBoard.length) * 100;
        winPercentage = (double) Math.round(winPercentage * 10) / 10;

        grade = giveGrade(winPercentage);
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

    public String returnGrade() {
        return grade;
    }

    public String giveGrade(double percent) {
        if (percent > 80) {
            return "A+";
        }
        else if (percent > 76) {
            return "A";
        }
        else if (percent > 72) {
            return "A-";
        }
        else if (percent > 68) {
            return "B+";
        }
        else if (percent > 64) {
            return "B";
        }
        else if (percent > 60) {
            return "B-";
        }
        else if (percent > 56) {
            return "C+";
        }
        else if (percent > 52) {
            return "C";
        }
        else if (percent > 48) {
            return "C-";
        }
        else if (percent > 44) {
            return "D+";
        }
        else if (percent > 40) {
            return "D";
        }
        else if (percent > 36) {
            return "D-";
        }
        else {
            return "F";
        }
    }
}
