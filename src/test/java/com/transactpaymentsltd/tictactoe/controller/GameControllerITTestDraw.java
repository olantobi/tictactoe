package com.transactpaymentsltd.tictactoe.controller;

import com.transactpaymentsltd.tictactoe.common.HeaderConstants;
import com.transactpaymentsltd.tictactoe.dto.GameStateDto;
import com.transactpaymentsltd.tictactoe.dto.NewGameDto;
import com.transactpaymentsltd.tictactoe.dto.PlaceMarkRequestDto;
import com.transactpaymentsltd.tictactoe.dto.PlaceMarkResponseDto;
import com.transactpaymentsltd.tictactoe.enumeration.GameStatus;
import com.transactpaymentsltd.tictactoe.enumeration.PlaceMarkStatus;
import com.transactpaymentsltd.tictactoe.repository.GameRepository;
import com.transactpaymentsltd.tictactoe.repository.PlayerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameControllerITTestDraw {
    @LocalServerPort
    private String port;

    @Autowired
    private TestRestTemplate client;

    private static String gameOwnerId;
    private static String gameJoinerId;

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @BeforeAll
    void initialize() {
        gameRepository.reset();
        playerRepository.reset();
    }

    @Test
    @Order(1)
    void testCreateGame() {
        int gameId = 1;
        String expectedInvitationUrl = getBaseUrl( "game/"+ gameId +"/join");

        ResponseEntity<NewGameDto> actual = this.client.postForEntity(getBaseUrl("/game"), null, NewGameDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getHeaders().get(HeaderConstants.SET_AUTH_TOKEN));
        Assertions.assertNotNull(actual.getBody().getInvitationUrl());
        Assertions.assertEquals(expectedInvitationUrl, actual.getBody().getInvitationUrl());

        gameOwnerId = actual.getHeaders().get(HeaderConstants.SET_AUTH_TOKEN).get(0);
    }

    @Test
    @Order(2)
    void testJoinGame() {
        int gameId = 1;

        String endpointUrl = getBaseUrl("game/"+gameId+"/join");
        ResponseEntity<GameStateDto> actual = this.client.postForEntity(endpointUrl, null, GameStateDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getHeaders().get(HeaderConstants.SET_AUTH_TOKEN));
        Assertions.assertNotNull(actual.getBody().getStatus());
        Assertions.assertEquals(GameStatus.YOUR_TURN, actual.getBody().getStatus());

        gameJoinerId = actual.getHeaders().get(HeaderConstants.SET_AUTH_TOKEN).get(0);
    }

    @Test
    @Order(3)
    void testPlayDraw() {
        gameJoinerPlays("A1");
        gameOwnerPlays("A3");
        gameJoinerPlays("B1");
        gameOwnerPlays("C1");
        gameJoinerPlays("B2");
        gameOwnerPlays("B3");
        gameJoinerPlays("A2");
        gameOwnerPlays("C2");
        gameJoinerPlays("C3");
    }

    @Test
    @Order(4)
    void testGetGameStateByGameJoinerShouldReturnDraw() {
        int gameId = 1;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameJoinerId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String endpointUrl = getBaseUrl("game/"+gameId);
        ResponseEntity<GameStateDto> actual = this.client.exchange(endpointUrl, HttpMethod.GET, requestEntity, GameStateDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getStatus());
        Assertions.assertEquals(GameStatus.DRAW, actual.getBody().getStatus());
    }

    @Test
    @Order(5)
    void testGetGameStateByGameOwnerShouldReturnDraw() {
        int gameId = 1;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameJoinerId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String endpointUrl = getBaseUrl("game/"+gameId);
        ResponseEntity<GameStateDto> actual = this.client.exchange(endpointUrl, HttpMethod.GET, requestEntity, GameStateDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getStatus());
        Assertions.assertEquals(GameStatus.DRAW, actual.getBody().getStatus());
    }

    private String getBaseUrl(final String action) {
        return String.format("http://localhost:%s/%s", this.port, action);
    }

    private void gameOwnerPlays(String position) {
        int gameId = 1;
        PlaceMarkRequestDto requestDto = new PlaceMarkRequestDto(position);

        String endpointUrl = getBaseUrl("game/"+gameId);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameOwnerId);
        HttpEntity<PlaceMarkRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        ResponseEntity<PlaceMarkResponseDto> actual = this.client.exchange(endpointUrl, HttpMethod.PUT, requestEntity, PlaceMarkResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getResult());
        Assertions.assertEquals(PlaceMarkStatus.OK, actual.getBody().getResult());
    }

    private void gameJoinerPlays(String position) {
        int gameId = 1;
        PlaceMarkRequestDto requestDto = new PlaceMarkRequestDto(position);

        String endpointUrl = getBaseUrl("game/"+gameId);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameJoinerId);
        HttpEntity<PlaceMarkRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        ResponseEntity<PlaceMarkResponseDto> actual = this.client.exchange(endpointUrl, HttpMethod.PUT, requestEntity, PlaceMarkResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getResult());
        Assertions.assertEquals(PlaceMarkStatus.OK, actual.getBody().getResult());
    }
}
