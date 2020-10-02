package com.liferon.tictactoe.repository;

import com.liferon.tictactoe.common.GameConstants;
import com.liferon.tictactoe.enumeration.GameStatus;
import com.liferon.tictactoe.enumeration.PlaceMarkStatus;
import com.liferon.tictactoe.exception.InvalidPositionException;
import com.liferon.tictactoe.model.Game;
import com.liferon.tictactoe.model.Player;
import com.liferon.tictactoe.util.GameUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MapGameRepository implements GameRepository {
    private final Map<Integer, Game> gameStore = new ConcurrentHashMap<>();
    private final AtomicInteger gameCounter = new AtomicInteger(0);

    public Optional<Game> getGame(int gameId) {
        return Optional.ofNullable(gameStore.get(gameId));
    }

    @Override
    public PlaceMarkStatus placeMark(Game game, Player player, String position) {
        int rowIndex = GameUtil.charToIndex(position.charAt(0));
        int colIndex = Character.getNumericValue(position.charAt(1)) - 1;

        if (rowIndex < 0 || rowIndex >= GameConstants.GAME_ROWS || colIndex < 0 || colIndex >= GameConstants.GAME_COLS) {
            throw new InvalidPositionException(position);
        }

        char[][] gameState = game.getGameState();

        if (gameState[rowIndex][colIndex] != 0) {
            return PlaceMarkStatus.SPACE_TAKEN;
        }

        if (player.isOwner()) {
            gameState[rowIndex][colIndex] = GameConstants.GAME_OWNERS_MARK;
            game.setStatus(GameStatus.JOINER_TURN);
        } else {
            gameState[rowIndex][colIndex] = GameConstants.GAME_JOINERS_MARK;
            game.setStatus(GameStatus.OWNER_TURN);
        }
        game.setGameState(gameState);

        gameStore.put(game.getId(), game);

        return PlaceMarkStatus.OK;
    }

    @Override
    public void updateGame(Game game) {
        gameStore.put(game.getId(), game);
    }

    public Game createGame() {
        int gameId = gameCounter.incrementAndGet();
        char[][] initialState = new char[GameConstants.GAME_ROWS][GameConstants.GAME_COLS];

        Game game = Game.builder().id(gameId)
                .status(GameStatus.AWAITING_OTHER_PLAYER)
                .gameState(initialState).build();

        gameStore.put(gameId, game);

        return game;
    }

    public void reset() {
        gameCounter.set(0);
        gameStore.clear();
    }
}
