package com.dajeom.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TarotSessionCreateRequest(

        @NotNull(message = "스프레드를 선택해주세요.")
        @Positive(message = "스프레드 번호가 올바르지 않습니다.")
        Integer spreadId,

        @Size(
                max = 1000,
                message = "질문은 1000자 이하로 입력해주세요."
        )
        String question,

        Boolean allowReversed

) {

    public boolean reversedEnabled() {
        return allowReversed == null
                || allowReversed;
    }
}