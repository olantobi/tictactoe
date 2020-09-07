package com.transactpaymentsltd.tictactoe.repository;

import com.transactpaymentsltd.tictactoe.common.GameConstants;
import com.transactpaymentsltd.tictactoe.model.Game;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MapGameRepository implements GameRepository {
    private final Map<Integer, char[][]> gameStore = new ConcurrentHashMap<>();
    private final AtomicInteger gameCounter = new AtomicInteger(0);

    public Optional<Game> getGame(int gameId) {
        char[][] gameState = gameStore.get(gameId);
        if (gameState == null) {
            return Optional.empty();
        }

        return Optional.of(Game.builder().id(gameId).gameState(gameState).build());
    }

    public Game createGame() {
        int gameId = gameCounter.incrementAndGet();
        char[][] createdGameState = gameStore.put(gameId, new char[GameConstants.GAME_ROWS][GameConstants.GAME_COLS]);

        return Game.builder().id(gameId).gameState(createdGameState).build();
    }
}
