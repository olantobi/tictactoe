package com.transactpaymentsltd.tictactoe.repository;

import com.transactpaymentsltd.tictactoe.enumeration.PlaceMarkStatus;
import com.transactpaymentsltd.tictactoe.model.Game;
import com.transactpaymentsltd.tictactoe.model.Player;

import java.util.Optional;

public interface GameRepository {
    Game createGame();
    Optional<Game> getGame(int gameId);
    PlaceMarkStatus placeMark(Game game, Player player, String position);
    void updateGame(Game game);
    void reset();
}
