package com.transactpaymentsltd.tictactoe.controller;

import com.transactpaymentsltd.tictactoe.common.Errors;
import com.transactpaymentsltd.tictactoe.common.HeaderConstants;
import com.transactpaymentsltd.tictactoe.enumeration.GameState;
import com.transactpaymentsltd.tictactoe.enumeration.PlaceMarkStatus;
import com.transactpaymentsltd.tictactoe.exception.InvalidGameException;
import com.transactpaymentsltd.tictactoe.model.Player;
import com.transactpaymentsltd.tictactoe.service.GameService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GameController.class)
public class GameControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    @SneakyThrows
    void testCreateGame() {
        UUID playerUuid = UUID.randomUUID();
        int gameId = 1;
        String expectedInvitationUrl = "http://localhost/game/"+ gameId +"/join";

        given(gameService.createGame()).willReturn(Player.builder()
                .gameId(gameId).playerSessionId(playerUuid).build());

        this.mockMvc.perform(post("/game"))
                .andExpect(status().isOk())
                .andExpect(header().string(HeaderConstants.SET_AUTH_TOKEN, playerUuid.toString()))
                .andExpect(jsonPath("$.invitationUrl", is(expectedInvitationUrl)));
    }

    @Test
    @SneakyThrows
    void testJoinGame() {
        UUID playerUuid = UUID.randomUUID();
        int gameId = 1;

        given(gameService.joinGame(gameId)).willReturn(Player.builder()
                .gameId(gameId).playerSessionId(playerUuid).build());

        this.mockMvc.perform(post("/game/"+gameId+"/join"))
                .andExpect(status().isOk())
                .andExpect(header().string(HeaderConstants.SET_AUTH_TOKEN, playerUuid.toString()))
                .andExpect(jsonPath("$.status", is(GameState.YOUR_TURN.name())));
    }

    @Test
    @SneakyThrows
    void testJoinInvalidGame() {
        int gameId = 1;

        given(gameService.joinGame(gameId)).willThrow(InvalidGameException.class);

        this.mockMvc.perform(post("/game/"+gameId+"/join"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description", is(Errors.INVALID_GAME.getMessage())));
    }

    @Test
    @SneakyThrows
    void testPlaceMark() {
        UUID playerUuid = UUID.randomUUID();
        int gameId = 1;

        given(gameService.createGame()).willReturn(Player.builder()
                .gameId(gameId).playerSessionId(playerUuid).build());

        this.mockMvc.perform(put("/game/"+gameId))
                .andExpect(status().isOk())
                .andExpect(header().string(HeaderConstants.SET_AUTH_TOKEN, playerUuid.toString()))
                .andExpect(jsonPath("$.result", is(PlaceMarkStatus.OK)));
    }

    @Test
    @SneakyThrows
    void testGetGameState() {
        UUID playerUuid = UUID.randomUUID();
        int gameId = 1;

        given(gameService.createGame()).willReturn(Player.builder()
                .gameId(gameId).playerSessionId(playerUuid).build());

        this.mockMvc.perform(get("/game/"+gameId))
                .andExpect(status().isOk())
                .andExpect(header().string(HeaderConstants.SET_AUTH_TOKEN, playerUuid.toString()))
                .andExpect(jsonPath("$.status", is(GameState.AWAITING_OTHER_PLAYER)));
    }
}
