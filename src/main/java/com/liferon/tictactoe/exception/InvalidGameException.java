package com.liferon.tictactoe.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvalidGameException extends RuntimeException {
    final int gameId;
}
