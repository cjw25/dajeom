package com.dajeom.dto;

import com.dajeom.model.HiddenStem;

/**
 * 지장간 한 개를 프론트에 반환하는 Response다.
 */
public record HiddenStemResponse(
        String branchPosition,
        String branchPositionKorean,
        String stem,
        String stemKorean,
        String role,
        String roleKorean,
        String element,
        String elementKorean
) {

    public static HiddenStemResponse from(
            HiddenStem hiddenStem
    ) {
        return new HiddenStemResponse(
                hiddenStem.branchPosition().name(),

                hiddenStem
                        .branchPosition()
                        .getKoreanName(),

                hiddenStem
                        .stem()
                        .getChineseCharacter(),

                hiddenStem
                        .stem()
                        .getKoreanName(),

                hiddenStem.role().name(),

                hiddenStem
                        .role()
                        .getKoreanName(),

                hiddenStem
                        .stem()
                        .getElement()
                        .name(),

                hiddenStem
                        .stem()
                        .getElement()
                        .getKoreanName()
        );
    }
}