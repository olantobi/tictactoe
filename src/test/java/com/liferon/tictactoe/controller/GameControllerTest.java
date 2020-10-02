package com.liferon.tictactoe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferon.tictactoe.common.Errors;
import com.liferon.tictactoe.common.HeaderConstants;
import com.liferon.tictactoe.dto.PlaceMarkRequestDto;
import com.liferon.tictactoe.enumeration.GameStatus;
import com.liferon.tictactoe.enumeration.PlaceMarkStatus;
import com.liferon.tictactoe.exception.InvalidGameException;
import com.liferon.tictactoe.model.Player;
import com.liferon.tictactoe.service.GameService;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(GameStatus.YOUR_TURN.name())));
    }

    @Test
    @SneakyThrows
    void testJoinInvalidGame() {
        int gameId = 1;

        given(gameService.joinGame(gameId)).willThrow(InvalidGameException.class);

        this.mockMvc.perform(post("/game/"+gameId+"/join"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(Errors.INVALID_GAME.getMessage())));
    }

    @Test
    @SneakyThrows
    void testPlaceMark() {
        UUID playerUuid = UUID.randomUUID();
        int gameId = 1;
        PlaceMarkRequestDto requestDto = new PlaceMarkRequestDto("A1");

        given(gameService.placeMark(any(), any(), any())).willReturn(PlaceMarkStatus.OK);

        this.mockMvc.perform(put("/game/"+gameId)
                .header(HeaderConstants.AUTH_TOKEN, playerUuid.toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(PlaceMarkStatus.OK.name())));
    }

    @Test
    @SneakyThrows
    void testGetGameState() {
        UUID playerUuid = UUID.randomUUID();
        int gameId = 1;

        given(gameService.getGameState(gameId, playerUuid.toString())).willReturn(GameStatus.AWAITING_OTHER_PLAYER);

        this.mockMvc.perform(get("/game/"+gameId)
                .header(HeaderConstants.AUTH_TOKEN, playerUuid.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(GameStatus.AWAITING_OTHER_PLAYER.name())));
    }
}
