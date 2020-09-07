package com.transactpaymentsltd.tictactoe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
public class PlaceMarkRequestDto {
    @NotEmpty
    private String position;
}
