package com.dajeom.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SajuAnalysisRequest(

        @NotNull(message = "사주 기본정보가 필요합니다.")
        @Positive(message = "올바른 사주 기본정보를 선택해주세요.")
        Long profileId
) {
}