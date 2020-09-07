package com.transactpaymentsltd.tictactoe.repository;

import com.transactpaymentsltd.tictactoe.model.Game;

import java.util.Optional;

public interface GameRepository {
    public Game createGame();
    public Optional<Game> getGame(int gameId);

}
