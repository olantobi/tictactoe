package com.transactpaymentsltd.tictactoe.service;

import com.transactpaymentsltd.tictactoe.common.GameConstants;
import com.transactpaymentsltd.tictactoe.dto.PlaceMarkRequestDto;
import com.transactpaymentsltd.tictactoe.enumeration.GameStatus;
import com.transactpaymentsltd.tictactoe.enumeration.PlaceMarkStatus;
import com.transactpaymentsltd.tictactoe.exception.AccessDeniedException;
import com.transactpaymentsltd.tictactoe.exception.InvalidGameException;
import com.transactpaymentsltd.tictactoe.exception.InvalidPlayerException;
import com.transactpaymentsltd.tictactoe.model.Game;
import com.transactpaymentsltd.tictactoe.model.Player;
import com.transactpaymentsltd.tictactoe.repository.GameRepository;
import com.transactpaymentsltd.tictactoe.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        game.setStatus(GameStatus.JOINER_TURN);
        gameRepository.updateGame(game);

        return playerRepository.addPlayer(gameId, false);
    }

    @Override
    public PlaceMarkStatus placeMark(Integer gameId, String authToken, PlaceMarkRequestDto placeMarkDto) {
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

        if ((player.isOwner() && game.getStatus() != GameStatus.OWNER_TURN) ||
                (!player.isOwner() && game.getStatus() != GameStatus.JOINER_TURN)) {
            return PlaceMarkStatus.OTHER_PLAYER_TURN;
        }

        PlaceMarkStatus placeMarkStatus = gameRepository.placeMark(game, player, placeMarkDto.getPosition());

        checkForWin(game);

        checkForDraw(game);

        return placeMarkStatus;
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
            case OWNER_TURN:
                return player.isOwner() ? GameStatus.YOUR_TURN : GameStatus.OTHER_PLAYER_TURN;
            case JOINER_TURN:
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

    private void checkForWin(Game game) {
        int[] topRow = {0, 0, 0, 1, 0, 2};
        int[] midRow = {1, 0, 1, 1, 1, 2};
        int[] bottomRow = {2, 0, 2, 1, 2, 2};
        int[] leftCol = {0, 0, 1, 0, 2, 0};
        int[] midCol = {0, 1, 1, 1,  2, 1};
        int[] rightCol = {0, 2, 1, 2, 2, 2};
        int[] backSlashCross = {0, 0, 1, 1, 2, 2};
        int[] forwardSlashCross = {0, 2, 1, 1, 2, 0};

        List<int[]> winConditions = new ArrayList<>();
        winConditions.add(topRow);
        winConditions.add(midRow);
        winConditions.add(bottomRow);
        winConditions.add(leftCol);
        winConditions.add(midCol);
        winConditions.add(rightCol);
        winConditions.add(backSlashCross);
        winConditions.add(forwardSlashCross);

        char[][] gameState = game.getGameState();

        for (int[] i : winConditions) {
            if (gameState[i[0]][i[1]] != 0 && gameState[i[0]][i[1]] == gameState[i[2]][i[3]] && gameState[i[2]][i[3]] == gameState[i[4]][i[5]]) {
                GameStatus status = gameState[i[0]][i[1]] == GameConstants.GAME_OWNERS_MARK ? GameStatus.OWNER_WON : GameStatus.JOINER_WON;
                game.setStatus(status);
                gameRepository.updateGame(game);
                break;
            }
        }
    }

    public void checkForDraw(Game game) {
        for (char [] row : game.getGameState()) {
            for (char c : row) {
               if (c == 0) {        // Empty board space
                   return;
               }
            }
        }

        game.setStatus(GameStatus.DRAW);
        gameRepository.updateGame(game);
    }
}
