package com.transactpaymentsltd.tictactoe.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Errors {
    INVALID_GAME("Invalid game specified");

    @Getter
    private final String message;

    public String format(final Object... args) {
        return String.format(this.message, args);
    }
}
