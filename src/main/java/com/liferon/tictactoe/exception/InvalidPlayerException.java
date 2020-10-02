package com.liferon.tictactoe.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvalidPlayerException extends RuntimeException {
    private final String playerSessionId;
}
