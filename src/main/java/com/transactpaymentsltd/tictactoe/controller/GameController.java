package com.transactpaymentsltd.tictactoe.controller;

import com.transactpaymentsltd.tictactoe.common.HeaderConstants;
import com.transactpaymentsltd.tictactoe.dto.GameStateDto;
import com.transactpaymentsltd.tictactoe.dto.NewGameDto;
import com.transactpaymentsltd.tictactoe.dto.PlaceMarkResponseDto;
import com.transactpaymentsltd.tictactoe.enumeration.GameState;
import com.transactpaymentsltd.tictactoe.enumeration.PlaceMarkStatus;
import com.transactpaymentsltd.tictactoe.model.Player;
import com.transactpaymentsltd.tictactoe.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequestMapping("/game")
@RestController
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping
    public ResponseEntity<?> createNewGame() {
        Player player = gameService.createGame();

        final String invitationUrl = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}/join")
                .buildAndExpand(player.getGameId()).toUriString();

        return ResponseEntity.ok()
                .header(HeaderConstants.SET_AUTH_TOKEN, player.getPlayerSessionId().toString())
                .body(new NewGameDto(invitationUrl));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinGame(@PathVariable("id") Integer id) {
        Player player = gameService.joinGame(id);

        return ResponseEntity.ok()
                .header(HeaderConstants.SET_AUTH_TOKEN, player.getPlayerSessionId().toString())
                .body(new GameStateDto(GameState.YOUR_TURN));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> placeMark(@PathVariable("id") Integer id) {
        gameService.placeMark(id);

        return ResponseEntity.ok(new PlaceMarkResponseDto(PlaceMarkStatus.OK));
    }

    @GetMapping("/{id")
    public ResponseEntity<?> getGameState(@PathVariable("id") Integer id) {
        gameService.getGameState(id);

        return ResponseEntity.ok(new GameStateDto(GameState.YOU_WON));
    }
}
