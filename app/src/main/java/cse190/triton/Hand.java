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

import static cse190.triton.NikiConstants.*;

public class Hand {
    public long hCards = 0; //bit mask for hand
    public long hKey = 1; //key for hand
    int wins = 0;

    Hand() {};
    
    Hand(long cards) {
        this.hCards = cards;
        for(int i = 0; i < DECK_BIT_MASKS.length; i++) {
            if((cards & DECK_BIT_MASKS[i]) == DECK_BIT_MASKS[i])
                hKey = hKey * PRIMES[i];
        }
    }

}
