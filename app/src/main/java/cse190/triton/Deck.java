/*
 * Copyright 2011 Petros Koutloubasis <koutloup@gmail.com>
 *
 * NIKI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * NIKI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NIKI. If not, see <http://www.gnu.org/licenses/>.
 */

package cse190.triton;

import java.util.Random;
import java.util.ArrayList;
import java.math.BigInteger;

import static cse190.triton.NikiConstants.*;

public class Deck {
    private ArrayList<Long> deck;
    private ArrayList<Integer> primes;
    public long[] keysBoard;
    public long[] handsBoard;

    public static void main(String[] args) {
        Deck d = new Deck();
    }

    public Deck() {
        deck = new ArrayList<Long>();
        primes = new ArrayList<Integer>();

        for(int i = 0; i < DECK_BIT_MASKS.length; i++) {
            deck.add(DECK_BIT_MASKS[i]);
            primes.add(PRIMES[i]);
        }
    }

    public void removeCards(long cards) {
        Long c;
        int i;
        while(cards != 0) {
            c = Long.highestOneBit(cards);
            i = deck.indexOf(c);
            deck.remove(c);
            primes.remove(i);
            cards = c^cards;
        }
    }

    public void enumRandom() {
        int count = 1;
        int size = 5;
        Long cards = 0L;
        Long key = 0L;
        Random r = new Random();
        keysBoard = new long[count];
        handsBoard = new long[count];

        int x = 0;
        long oldCards = 0L;
        for(int i = 0; i < count; i++) {
            cards = 0L;
            key = 1L;
            while(Long.bitCount(cards) != size) {
                x = r.nextInt(deck.size());
                oldCards = cards;
                cards = cards | (Long) deck.get(x);
                if(cards != oldCards)
                    key = 1L * key * primes.get(x);
            }
            handsBoard[i] = cards;
            keysBoard[i] = key;
        }
    }

    public void enumRandom(int temp) {
        int count = 1;
        int size = temp;
        Long cards = 0L;
        Long key = 0L;
        Random r = new Random();
        keysBoard = new long[count];
        handsBoard = new long[count];

        int x = 0;
        long oldCards = 0L;
        for(int i = 0; i < count; i++) {
            cards = 0L;
            key = 1L;
            while(Long.bitCount(cards) != size) {
                x = r.nextInt(deck.size());
                oldCards = cards;
                cards = cards | (Long) deck.get(x);
                if(cards != oldCards)
                    key = 1L * key * primes.get(x);
            }
            handsBoard[i] = cards;
            keysBoard[i] = key;
        }
    }

    public void enumRandom(int size, int count) {
        Long cards = 0L;
        Long key = 0L;
        Random r = new Random();
        keysBoard = new long[count];
        handsBoard = new long[count];

        int x = 0;
        long oldCards = 0L;
        for(int i = 0; i < count; i++) {
            cards = 0L;
            key = 1L;
            while(Long.bitCount(cards) != size) {
                x = r.nextInt(deck.size());
                oldCards = cards;
                cards = cards | (Long) deck.get(x);
                if(cards != oldCards)
                    key = 1L * key * primes.get(x);
            }
            handsBoard[i] = cards;
            keysBoard[i] = key;
        }
    }

    public long getBoard() {
        return handsBoard[0];
    }

}
