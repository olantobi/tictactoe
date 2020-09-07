package com.transactpaymentsltd.tictactoe.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvalidPositionException extends RuntimeException {
    private final String position;
}
