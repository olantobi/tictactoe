package com.transactpaymentsltd.tictactoe.repository;

import com.transactpaymentsltd.tictactoe.model.Player;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MapPlayerRepository implements PlayerRepository {
    private final Map<UUID, Player> playerStore = new ConcurrentHashMap<>();

    public Player addPlayer(int gameId, boolean isOwner) {
        UUID playerSessionId = UUID.randomUUID();
        Player player = Player.builder()
                .gameId(gameId).isOwner(isOwner)
                .playerSessionId(playerSessionId).build();

        playerStore.put(playerSessionId, player);

        return Player.builder().gameId(gameId).playerSessionId(playerSessionId).build();
    }

    public Optional<Player> getPlayer(UUID playerSessionId) {
        return Optional.ofNullable(playerStore.get(playerSessionId));
    }

    @Override
    public Optional<Player> getPlayer(String playerSessionId) {
        return getPlayer(UUID.fromString(playerSessionId));
    }

    public void reset() {
        playerStore.clear();
    }
}
