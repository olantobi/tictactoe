package com.transactpaymentsltd.tictactoe.repository;

import com.transactpaymentsltd.tictactoe.common.GameConstants;
import com.transactpaymentsltd.tictactoe.enumeration.GameStatus;
import com.transactpaymentsltd.tictactoe.enumeration.PlaceMarkStatus;
import com.transactpaymentsltd.tictactoe.exception.InvalidPositionException;
import com.transactpaymentsltd.tictactoe.model.Game;
import com.transactpaymentsltd.tictactoe.model.Player;
import com.transactpaymentsltd.tictactoe.util.GameUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MapGameRepository implements GameRepository {
    private final Map<Integer, Game> gameStore = new ConcurrentHashMap<>();
    private final AtomicInteger gameCounter = new AtomicInteger(0);
    private static final List<Integer> ownerPositions = new ArrayList<>();
    private static final List<Integer> joinerPositions = new ArrayList<>();

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
            game.setStatus(GameStatus.JOINERS_TURN);
        } else {
            gameState[rowIndex][colIndex] = GameConstants.GAME_JOINERS_MARK;
            game.setStatus(GameStatus.OWNERS_TURN);
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
}
