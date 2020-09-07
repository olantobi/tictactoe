package com.transactpaymentsltd.tictactoe.service;

import com.transactpaymentsltd.tictactoe.model.Player;

public interface GameService {
    Player createGame();
    Player joinGame(Integer gameId);
    void placeMark(Integer gameId);
    void getGameState(Integer gameId);
}
