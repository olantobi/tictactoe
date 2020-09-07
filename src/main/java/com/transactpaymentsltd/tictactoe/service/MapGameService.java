package com.transactpaymentsltd.tictactoe.service;

import com.transactpaymentsltd.tictactoe.dto.PlaceMarkRequestDto;
import com.transactpaymentsltd.tictactoe.enumeration.GameStatus;
import com.transactpaymentsltd.tictactoe.exception.AccessDeniedException;
import com.transactpaymentsltd.tictactoe.exception.InvalidGameException;
import com.transactpaymentsltd.tictactoe.exception.InvalidPlayerException;
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
        return playerRepository.addPlayer(game.getId(), true);
    }

    @Override
    public Player joinGame(Integer gameId) {
        Optional<Game> gameOptional = gameRepository.getGame(gameId);
        if (!gameOptional.isPresent()) {
            throw new InvalidGameException(gameId);
        }

        Game game = gameOptional.get();
        game.setStatus(GameStatus.JOINERS_TURN);
        gameRepository.updateGame(game);

        return playerRepository.addPlayer(gameId, false);
    }

    @Override
    public boolean placeMark(Integer gameId, String authToken, PlaceMarkRequestDto placeMarkDto) {
        Optional<Game> gameOptional = gameRepository.getGame(gameId);
        if (!gameOptional.isPresent()) {
            throw new InvalidGameException(gameId);
        }

        Optional<Player> playerOptional = playerRepository.getPlayer(authToken);
        if (!playerOptional.isPresent()) {
            throw new InvalidPlayerException(authToken);
        }

        Player player = playerOptional.get();
        if (player.getGameId().intValue() != gameId.intValue()) {
            throw new AccessDeniedException();
        }

        Game game = gameOptional.get();
        gameRepository.placeMark(game, player, placeMarkDto.getPosition());

        return true;
    }

    @Override
    public GameStatus getGameState(Integer gameId, String authToken) {
        Optional<Game> gameOptional = gameRepository.getGame(gameId);
        if (!gameOptional.isPresent()) {
            throw new InvalidGameException(gameId);
        }

        Optional<Player> playerOptional = playerRepository.getPlayer(authToken);
        if (!playerOptional.isPresent()) {
            throw new InvalidPlayerException(authToken);
        }

        Player player = playerOptional.get();
        if (player.getGameId().intValue() != gameId.intValue()) {
            throw new AccessDeniedException();
        }

        Game game = gameOptional.get();

        switch (game.getStatus()) {
            case OWNERS_TURN:
                return player.isOwner() ? GameStatus.YOUR_TURN : GameStatus.OTHER_PLAYER_TURN;
            case JOINERS_TURN:
                return player.isOwner() ? GameStatus.OTHER_PLAYER_TURN : GameStatus.YOUR_TURN;
            case OWNER_WON:
                return player.isOwner() ? GameStatus.YOU_WON : GameStatus.YOU_LOST;
            case JOINER_WON:
                return player.isOwner() ? GameStatus.YOU_LOST : GameStatus.YOU_WON;
            case AWAITING_OTHER_PLAYER:
            case DRAW:
            default:
                return game.getStatus();
        }
    }
}
