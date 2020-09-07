package com.transactpaymentsltd.tictactoe.dto;

import com.transactpaymentsltd.tictactoe.enumeration.PlaceMarkStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceMarkResponseDto {
    private PlaceMarkStatus result;
}
