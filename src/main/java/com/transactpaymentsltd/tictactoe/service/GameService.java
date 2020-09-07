package com.transactpaymentsltd.tictactoe.service;

import com.transactpaymentsltd.tictactoe.dto.PlaceMarkRequestDto;
import com.transactpaymentsltd.tictactoe.enumeration.GameStatus;
import com.transactpaymentsltd.tictactoe.enumeration.PlaceMarkStatus;
import com.transactpaymentsltd.tictactoe.model.Player;

public interface GameService {
    Player createGame();
    Player joinGame(Integer gameId);
    PlaceMarkStatus placeMark(Integer gameId, String authToken, PlaceMarkRequestDto placeMarkDto);
    GameStatus getGameState(Integer gameId, String authToken);
}
