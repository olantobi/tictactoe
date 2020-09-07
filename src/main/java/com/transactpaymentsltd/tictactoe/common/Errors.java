package com.transactpaymentsltd.tictactoe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Errors {
    INVALID_GAME("Invalid game id specified"),

    INVALID_PLAYER("Invalid auth-token header specified"),

    GAME_ACCESS_DENIED("Sorry, you do not have access to this game");

    @Getter
    private final String message;

    public String format(final Object... args) {
        return String.format(this.message, args);
    }
}
