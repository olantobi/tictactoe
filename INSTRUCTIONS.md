# Transact Payments LTD

## Programming task for backend Java developer

Write a simple REST API allowing to play a tic-tac-toe game over 3x3 board allowing
for multiple games to be played simultaneously.

If you are not familiar with tic-tac-toe, you can find the rules here:
https://en.wikipedia.org/wiki/Tic-tac-toe

### "Happy path" scenario for a game:

1. Player A sends `POST` request to `/game` endpoint. The application will generate
a new game id and will respond with `Set-Auth-Token` HTTP header with the player's
token and HTTP body containing an invitation URL for the player B in the form:
`<rootURL>/game/{id}/join`
2. Player B sends `POST` request to the invitation URL. The response  has `Set-Auth-Token` 
HTTP header set to the player's token, and the HTTP body contains the initial status of the game.
3. The invited player (B) starts the game.
4. The players take turns to position their marks on the board by sending `PUT` to 
`/game/{id}` URL with `Auth-Token` header set to their tokens. The body contains the position
where the mark should be placed: `{"position":"A1"}`. The position value is in the format
`[ABC][123]`. First symbol `[ABC]` specifies the row and second symbol `[123]` specifies the column:
    ```
     1 2 3
    A_|_|_
    B_|_|_
    C | | 
    ```
    If the mark has been placed correctly, the server responds with status 200 and `OK` response.
    If there's another mark in the same position the server responds with status 200 and `SPACE_TAKEN` 
    response.
5. Players can check the game status by sending `GET` to `/game/{id}` url with their token.


### API details with example payloads:

- create new game
    - Request: POST `/game`
    - Response: header: `Set-Auth-Token`, body: `{"invitationUrl":"<rootURL>/game/{id}/join"}`
- join game
    - Request: POST `/game/{id}/join`
    - Response: header: `Set-Auth-Token`, body: `{"status":"YOUR_TURN"}`
- placing a mark
    - Request: PUT `/game/{id}`, header: `Auth-Token`, body: `{"position":"A1"}`
    - Response:
        - `{"result": "SPACE_TAKEN"}` when there's already another mark placed in the requested position
        - `{"result": "OK"}` when the mark has been placed correctly
- game state
    - Request: GET `/game/{id}`, header: `Auth-Token`
    - Response:
        - `{"status":"AWAITING_OTHER_PLAYER"}` when other player hasn't joined yet
        - `{"status":"YOUR_TURN"}` when it's this player's turn
        - `{"status":"OTHER_PLAYER_TURN"}` when it's the other player's turn
        - `{"status":"YOU_WON"}` when this player won the game 
        - `{"status":"YOU_LOST"}` when the other player won the game
        - `{"status":"DRAW"}` when there's no more space left on the board and noone won.
        
All errors (bad payloads, missing/bad tokens, trying to join non existing game, etc) should
result with response with HTTP status other than 2xx with the error description in the response body
in json format.

### Technical requirements:

- implemented in Java 8+
- built by Maven or Gradle
- you can use any frameworks/libs as you please
- you can't use any databases including in-memory database like H2, SQLite, etc.
  Restarting the server will lose all the games, that's fine.
- packaged as a Docker image

### What you should include in your solution:
- `build.sh` script to build the project
- `run.sh` script to start the app and print on the standard output
  the address where it can be accessed. The port mapping to docker host should be random
  (potentially different on each app run)
- `README.md` file with all the assumptions you made, and the following questions answered:
    - What libraries and/or frameworks you chose for the project and why?
    - Which part of the task you found the most difficult. Why? How did you solve it?
    - What would you need to change in the code if the next feature was to generalize the game to NxN size boards?
    

