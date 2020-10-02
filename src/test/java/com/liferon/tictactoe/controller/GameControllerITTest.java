package com.liferon.tictactoe.controller;

import com.liferon.tictactoe.common.Errors;
import com.liferon.tictactoe.common.HeaderConstants;
import com.liferon.tictactoe.dto.*;
import com.liferon.tictactoe.enumeration.GameStatus;
import com.liferon.tictactoe.enumeration.PlaceMarkStatus;
import com.liferon.tictactoe.repository.GameRepository;
import com.liferon.tictactoe.repository.PlayerRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameControllerITTest {
    @LocalServerPort
    private String port;

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;

    private static String gameOwnerId;
    private static String gameJoinerId;

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
    void testJoinInvalidGame() {
        int gameId = 2;

        String endpointUrl = getBaseUrl("game/"+gameId+"/join");
        ResponseEntity<ErrorDto> actual = this.client.postForEntity(endpointUrl, null, ErrorDto.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        Assertions.assertNull(actual.getHeaders().get(HeaderConstants.SET_AUTH_TOKEN));
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertEquals(Errors.INVALID_GAME.getMessage(), actual.getBody().getDescription());
    }

    @Test
    @Order(4)
    void testPlaceMarkByGameOwnerShouldReturnOtherPlayerTurn() {
        int gameId = 1;
        PlaceMarkRequestDto requestDto = new PlaceMarkRequestDto("A1");

        String endpointUrl = getBaseUrl("game/"+gameId);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameOwnerId);
        HttpEntity<PlaceMarkRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        ResponseEntity<PlaceMarkResponseDto> actual = this.client.exchange(endpointUrl, HttpMethod.PUT, requestEntity, PlaceMarkResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getResult());
        Assertions.assertEquals(PlaceMarkStatus.OTHER_PLAYER_TURN, actual.getBody().getResult());
    }

    @Test
    @Order(5)
    void testPlaceMarkByGameJoiner() {
        int gameId = 1;
        PlaceMarkRequestDto requestDto = new PlaceMarkRequestDto("A1");

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

    @Test
    @Order(6)
    void testPlaceMarkByGameOwnerSamePositionShouldReturnSpaceTaken() {
        int gameId = 1;
        PlaceMarkRequestDto requestDto = new PlaceMarkRequestDto("A1");

        String endpointUrl = getBaseUrl("game/"+gameId);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameOwnerId);
        HttpEntity<PlaceMarkRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        ResponseEntity<PlaceMarkResponseDto> actual = this.client.exchange(endpointUrl, HttpMethod.PUT, requestEntity, PlaceMarkResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getResult());
        Assertions.assertEquals(PlaceMarkStatus.SPACE_TAKEN, actual.getBody().getResult());
    }

    @Test
    @Order(7)
    void testPlaceMarkByGameOwnerValid() {
        int gameId = 1;
        PlaceMarkRequestDto requestDto = new PlaceMarkRequestDto("A3");

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

    @Test
    @Order(8)
    void testPlaceMarkByWithInvalidPosition() {
        int gameId = 1;
        PlaceMarkRequestDto requestDto = new PlaceMarkRequestDto("A4");

        String endpointUrl = getBaseUrl("game/"+gameId);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameJoinerId);
        HttpEntity<PlaceMarkRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        ResponseEntity<ErrorDto> actual = this.client.exchange(endpointUrl, HttpMethod.PUT, requestEntity, ErrorDto.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getDescription());
        Assertions.assertEquals(Errors.INVALID_POSITION.getMessage(), actual.getBody().getDescription());
    }

    @Test
    @Order(9)
    void testGetGameStateByGameJoinerShouldReturnYourTurn() {
        int gameId = 1;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameJoinerId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String endpointUrl = getBaseUrl("game/"+gameId);
        ResponseEntity<GameStateDto> actual = this.client.exchange(endpointUrl, HttpMethod.GET, requestEntity, GameStateDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getStatus());
        Assertions.assertEquals(GameStatus.YOUR_TURN, actual.getBody().getStatus());
    }

    @Test
    @Order(10)
    void testGetGameStateByGameOwnerShouldReturnYourTurn() {
        int gameId = 1;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameOwnerId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String endpointUrl = getBaseUrl("game/"+gameId);
        ResponseEntity<GameStateDto> actual = this.client.exchange(endpointUrl, HttpMethod.GET, requestEntity, GameStateDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getStatus());
        Assertions.assertEquals(GameStatus.OTHER_PLAYER_TURN, actual.getBody().getStatus());
    }

    @Test
    @Order(11)
    void testGameOwnerWins() {
        gameJoinerPlays("C2");
        gameOwnerPlays("B2");
        gameJoinerPlays("C3");
        gameOwnerPlays("C1");
    }

    @Test
    @Order(12)
    void testGetGameStateByGameJoinerShouldReturnYouLost() {
        int gameId = 1;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameJoinerId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String endpointUrl = getBaseUrl("game/"+gameId);
        ResponseEntity<GameStateDto> actual = this.client.exchange(endpointUrl, HttpMethod.GET, requestEntity, GameStateDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getStatus());
        Assertions.assertEquals(GameStatus.YOU_LOST, actual.getBody().getStatus());
    }

    @Test
    @Order(13)
    void testGetGameStateByGameOwnerShouldReturnYouWon() {
        int gameId = 1;

        HttpHeaders headers = new HttpHeaders();
        headers.set(HeaderConstants.AUTH_TOKEN, gameOwnerId);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String endpointUrl = getBaseUrl("game/"+gameId);
        ResponseEntity<GameStateDto> actual = this.client.exchange(endpointUrl, HttpMethod.GET, requestEntity, GameStateDto.class);

        Assertions.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Assertions.assertNotNull(actual.getBody().getStatus());
        Assertions.assertEquals(GameStatus.YOU_WON, actual.getBody().getStatus());
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
