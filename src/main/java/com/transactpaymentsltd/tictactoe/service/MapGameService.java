package com.transactpaymentsltd.tictactoe.service;

import com.transactpaymentsltd.tictactoe.exception.InvalidGameException;
import com.transactpaymentsltd.tictactoe.model.Game;
import com.transactpaymentsltd.tictactoe.model.Player;
import com.transactpaymentsltd.tictactoe.repository.GameRepository;
import com.transactpaymentsltd.tictactoe.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MapGameService implements GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    @Override
    public Player createGame() {
        Game game = gameRepository.createGame();
        return playerRepository.addPlayer(game.getId());
    }

    @Override
    public Player joinGame(Integer gameId) {
        Optional<Game> gameOptional = gameRepository.getGame(gameId);
        if (!gameOptional.isPresent()) {
            throw new InvalidGameException(gameId);
        }

        return playerRepository.addPlayer(gameOptional.get().getId());
    }

    @Override
    public void placeMark(Integer gameId) {

    }

    @Override
    public void getGameState(Integer gameId) {

    }
}
