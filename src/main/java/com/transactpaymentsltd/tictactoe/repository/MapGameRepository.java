package com.transactpaymentsltd.tictactoe.repository;

import com.transactpaymentsltd.tictactoe.common.GameConstants;
import com.transactpaymentsltd.tictactoe.enumeration.GameStatus;
import com.transactpaymentsltd.tictactoe.model.Game;
import com.transactpaymentsltd.tictactoe.model.Player;
import com.transactpaymentsltd.tictactoe.util.GameUtil;
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
    public void placeMark(Game game, Player player, String position) {
        char[][] gameState = game.getGameState();
        int rowIndex = GameUtil.charToIndex(position.charAt(0));
        int colIndex = position.charAt(1);

        if (player.isOwner()) {
            gameState[rowIndex][colIndex] = GameConstants.GAME_OWNERS_MARK;
            game.setStatus(GameStatus.JOINERS_TURN);
        } else {
            gameState[rowIndex][colIndex] = GameConstants.GAME_JOINERS_MARK;
            game.setStatus(GameStatus.OWNERS_TURN);
        }
        game.setGameState(gameState);

        gameStore.put(game.getId(), game);
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
