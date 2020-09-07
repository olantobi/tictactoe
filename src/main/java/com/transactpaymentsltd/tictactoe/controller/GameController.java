package com.transactpaymentsltd.tictactoe.controller;

import com.transactpaymentsltd.tictactoe.common.HeaderConstants;
import com.transactpaymentsltd.tictactoe.dto.GameStateDto;
import com.transactpaymentsltd.tictactoe.dto.NewGameDto;
import com.transactpaymentsltd.tictactoe.dto.PlaceMarkRequestDto;
import com.transactpaymentsltd.tictactoe.dto.PlaceMarkResponseDto;
import com.transactpaymentsltd.tictactoe.enumeration.GameStatus;
import com.transactpaymentsltd.tictactoe.enumeration.PlaceMarkStatus;
import com.transactpaymentsltd.tictactoe.model.Player;
import com.transactpaymentsltd.tictactoe.service.GameService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@RequestMapping("/game")
@RestController
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @ApiOperation("Create new game.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns response header and invitation url", response = NewGameDto.class)
    })
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

    @ApiOperation("Join game.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns response header and game status", response = GameStateDto.class)
    })
    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinGame(@PathVariable("id") Integer id) {
        Player player = gameService.joinGame(id);

        return ResponseEntity.ok()
                .header(HeaderConstants.SET_AUTH_TOKEN, player.getPlayerSessionId().toString())
                .body(new GameStateDto(GameStatus.YOUR_TURN));
    }

    @ApiOperation("Placing a mark.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns result OK or SPACE_TAKEN if there's another mark placed in the requested position", response = PlaceMarkResponseDto.class)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> placeMark(@PathVariable("id") Integer id,
                                       @RequestHeader(HeaderConstants.AUTH_TOKEN) String authToken,
                                       @Valid @RequestBody PlaceMarkRequestDto placeMarkDto) {
        PlaceMarkStatus placeMarkStatus = gameService.placeMark(id, authToken, placeMarkDto);

        return ResponseEntity.ok(new PlaceMarkResponseDto(placeMarkStatus));
    }

    @ApiOperation("Game state.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returns the status of the game", response = GameStateDto.class)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getGameState(@PathVariable("id") Integer id,
                                          @RequestHeader(HeaderConstants.AUTH_TOKEN) String authToken) {
        GameStatus gameState = gameService.getGameState(id, authToken);

        return ResponseEntity.ok(new GameStateDto(gameState));
    }
}
