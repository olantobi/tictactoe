package com.transactpaymentsltd.tictactoe.repository;

import com.transactpaymentsltd.tictactoe.model.Player;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MapPlayerRepository implements PlayerRepository {
    private final Map<UUID, Integer> playerStore = new ConcurrentHashMap<>();

    public Player addPlayer(int gameId) {
        UUID playerSessionId = UUID.randomUUID();
        playerStore.put(playerSessionId, gameId);

        return Player.builder().gameId(gameId).playerSessionId(playerSessionId).build();
    }

    public Player getPlayer(UUID playerSessionId) {
        Integer gameId = playerStore.get(playerSessionId);

        return Player.builder().gameId(gameId).playerSessionId(playerSessionId).build();
    }

    @Override
    public Player getPlayer(String playerSessionId) {
        return getPlayer(UUID.fromString(playerSessionId));
    }
}
