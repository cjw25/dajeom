package com.dajeom.dto;

import com.dajeom.type.FiveElement;

/**
 * 오행 하나의 세력 점수와 비율을 반환한다.
 */
public record ElementStrengthItemResponse(
        String element,
        String elementKorean,
        String elementChinese,
        double score,
        double percentage
) {

    public static ElementStrengthItemResponse of(
            FiveElement element,
            double score,
            double percentage
    ) {
        return new ElementStrengthItemResponse(
                element.name(),
                element.getKoreanName(),
                element.getChineseCharacter(),
                score,
                percentage
        );
    }
}