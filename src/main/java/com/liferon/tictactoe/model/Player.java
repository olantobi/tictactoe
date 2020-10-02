package com.liferon.tictactoe.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Player {
    private UUID playerSessionId;
    private Integer gameId;
    private boolean isOwner;
}
