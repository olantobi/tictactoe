package com.transactpaymentsltd.tictactoe.util;

import com.transactpaymentsltd.tictactoe.common.GameConstants;

public class GameUtil {
    public static int charToIndex(char c) {
        int numValue = Character.getNumericValue(c);
        int index = numValue % 10;
        if (!Character.isUpperCase(c) || index >= GameConstants.GAME_ROWS) {
            return -1;
        }

        return index;
    }
}
