package com.liferon.tictactoe.dto;

import com.liferon.tictactoe.enumeration.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameStateDto {
    private GameStatus status;
}
