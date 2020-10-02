package com.liferon.tictactoe.repository;

import com.liferon.tictactoe.model.Player;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {
    Player addPlayer(int gameId, boolean isOwner);
    Optional<Player> getPlayer(UUID playerSessionId);
    Optional<Player> getPlayer(String playerSessionId);
    void reset();
}
