package com.liferon.tictactoe.repository;

import com.liferon.tictactoe.enumeration.PlaceMarkStatus;
import com.liferon.tictactoe.model.Game;
import com.liferon.tictactoe.model.Player;

import java.util.Optional;

public interface GameRepository {
    Game createGame();
    Optional<Game> getGame(int gameId);
    PlaceMarkStatus placeMark(Game game, Player player, String position);
    void updateGame(Game game);
    void reset();
}
