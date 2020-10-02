package com.liferon.tictactoe.service;

import com.liferon.tictactoe.dto.PlaceMarkRequestDto;
import com.liferon.tictactoe.enumeration.GameStatus;
import com.liferon.tictactoe.enumeration.PlaceMarkStatus;
import com.liferon.tictactoe.model.Player;

public interface GameService {
    Player createGame();
    Player joinGame(Integer gameId);
    PlaceMarkStatus placeMark(Integer gameId, String authToken, PlaceMarkRequestDto placeMarkDto);
    GameStatus getGameState(Integer gameId, String authToken);
}
