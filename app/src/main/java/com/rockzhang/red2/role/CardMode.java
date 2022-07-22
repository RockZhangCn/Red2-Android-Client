package com.rockzhang.red2.role;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardMode {
    public static CardMode MODE_INVALID = new CardMode(-1);
    public static CardMode MODE_SINGLE = new CardMode(1);
    public static CardMode MODE_PAIR = new CardMode(2);
    public static CardMode MODE_THREE = new CardMode(3);
    public static CardMode MODE_THREE_ONE = new CardMode(4);
    public static CardMode MODE_THREE_TWE = new CardMode(5);
    public static CardMode MODE_AIRPLANE_NONE = new CardMode(6);
    public static CardMode MODE_AIRPLANE_ONE = new CardMode(7);
    public static CardMode MODE_AIRPLANE_TWE = new CardMode(8);
    public static CardMode MODE_SINGLE_LONG = new CardMode(9);
    public static CardMode MODE_PAIR_LONG = new CardMode(10);
    public static CardMode MODE_BOMB = new CardMode(11);
    public static CardMode MODE_TWO_RED2 = new CardMode(12);

    private int mInternalValue = -1;

    private CardMode(int status) {
        mInternalValue = status;
    }

    public static CardMode fromValue(int value) {
        return new CardMode(value);
    }

    public static CardMode getCardMode(List<Integer> cards) {
        Collections.sort(cards, Collections.reverseOrder());

        Set<Integer> valueSet = new HashSet<>(8);
        for (Integer i : cards) {
            valueSet.add(i);
        }

        int cnt = cards.size();
        int valueCount = valueSet.size();

        if (cnt == 0) {
            return MODE_INVALID;
        } else if (cnt == 1) {
            return MODE_SINGLE;
        } else if (cnt == 2) {
            if (valueCount == 1) {
                if ((cards.get(0) == 48) && (cards.get(1) == 48)) {
                    return MODE_TWO_RED2;
                } else {
                    return MODE_PAIR;
                }
            } else {
                return MODE_INVALID;
            }
        } else if (cnt == 3) {
            if (valueCount == 1) {
                return MODE_THREE;
            } else {
                return MODE_INVALID;
            }
        } else if (cnt == 4) {
            if (valueCount == 1) {
                return MODE_BOMB;
            } else if (valueCount == 2) {
                if ((cards.get(0).equals(cards.get(1))) && (cards.get(1).equals(cards.get(2)))) {
                    return MODE_THREE_ONE;
                } else if ((cards.get(1).equals(cards.get(2))) && (cards.get(2).equals(cards.get(3)))) {
                    return MODE_THREE_ONE;
                } else {
                    return MODE_INVALID;
                }
            } else {
                return MODE_INVALID;
            }
        } else if (cnt == 5) {
            if (valueCount == 1) {
                return MODE_BOMB;
            } else if (valueCount == 2) {
                if ((cards.get(0).equals(cards.get(1))) && (cards.get(1).equals(cards.get(2)))) {
                    return MODE_THREE_TWE;
                } else if ((cards.get(2).equals(cards.get(3))) && (cards.get(3).equals(cards.get(4)))) {
                    return MODE_THREE_TWE;
                } else {
                    return MODE_INVALID;
                }
            } else {
                return MODE_INVALID;
            }
        } else {
            if (valueCount == 1) {
                return MODE_BOMB;
            } else if ((valueCount == cnt)
                    && (
                    (Math.abs(cards.get(0) / 4 - cards.get(cnt - 1) / 4)) == (cnt - 1)
            )
            ) {
                return MODE_SINGLE_LONG;
            } else if ((valueCount == cnt / 2) && (cnt % 2 == 0)) {
                return MODE_PAIR_LONG;
            } else if ((valueCount == cnt / 3) && (cnt % 3 == 0)) {
                return MODE_AIRPLANE_NONE;
            } else if ((valueCount == cnt / 4) && (cnt % 4 == 0)) {
                return MODE_AIRPLANE_ONE;
            } else if ((valueCount == cnt / 5) && (cnt % 5 == 0)) {
                return MODE_AIRPLANE_TWE;
            } else {
                return MODE_INVALID;
            }
        }

    }

    public int getValue() {
        return mInternalValue;
    }

}
