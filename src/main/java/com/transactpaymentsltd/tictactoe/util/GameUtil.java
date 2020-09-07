package com.transactpaymentsltd.tictactoe.util;

import com.transactpaymentsltd.tictactoe.common.GameConstants;

public class GameUtil {
    public static int charToIndex(char c) {
        int numValue = Character.getNumericValue(c);
        if (Character.isUpperCase('c') || numValue >= GameConstants.GAME_ROWS) {
            return -1;
        }

        return numValue % 10;
    }
}
