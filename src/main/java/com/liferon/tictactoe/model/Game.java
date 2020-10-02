package com.liferon.tictactoe.model;

import com.liferon.tictactoe.enumeration.GameStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Game {
    private Integer id;
    private char[][] gameState = new char[3][3];
    private GameStatus status;
}
