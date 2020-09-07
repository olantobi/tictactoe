package com.transactpaymentsltd.tictactoe.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Game {
    private Integer id;
    private char[][] gameState = new char[3][3];
}
