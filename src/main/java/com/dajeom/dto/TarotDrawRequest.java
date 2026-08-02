package com.dajeom.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TarotDrawRequest(

        @NotNull(message = "선택한 카드 위치가 없습니다.")
        @Min(
                value = 0,
                message = "카드 위치가 올바르지 않습니다."
        )
        Integer slotIndex

) {
}