package com.transactpaymentsltd.tictactoe.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Player {
    private UUID playerSessionId;
    private Game game;
    private Integer gameId;
}
