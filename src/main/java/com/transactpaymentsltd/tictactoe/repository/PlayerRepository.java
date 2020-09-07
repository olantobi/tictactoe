package com.transactpaymentsltd.tictactoe.repository;

import com.transactpaymentsltd.tictactoe.model.Player;

import java.util.UUID;

public interface PlayerRepository {
    Player addPlayer(int gameId);
    Player getPlayer(UUID playerSessionId);
    Player getPlayer(String playerSessionId);
}
