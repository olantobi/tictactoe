package com.transactpaymentsltd.tictactoe.model;

import com.transactpaymentsltd.tictactoe.enumeration.GameStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Game {
    private Integer id;
    private char[][] gameState = new char[3][3];
    private GameStatus status;
}
