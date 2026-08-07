package com.dajeom.model;

import com.dajeom.type.LuckInteractionSource;
import com.dajeom.type.RelationType;

import java.util.Objects;

/**
 * 원국·대운·세운 사이에서 발생한 관계 하나를 나타낸다.
 */
public record LuckInteraction(
        LuckInteractionSource source,
        RelationType relationType,
        String firstLabel,
        String firstValue,
        String secondLabel,
        String secondValue,
        double scoreChange,
        boolean favorable,
        String description
) {

    public LuckInteraction {
        Objects.requireNonNull(
                source,
                "상호작용 출처는 필수입니다."
        );

        Objects.requireNonNull(
                relationType,
                "관계 유형은 필수입니다."
        );

        Objects.requireNonNull(
                firstLabel,
                "첫 번째 대상 이름은 필수입니다."
        );

        Objects.requireNonNull(
                firstValue,
                "첫 번째 대상 값은 필수입니다."
        );

        Objects.requireNonNull(
                secondLabel,
                "두 번째 대상 이름은 필수입니다."
        );

        Objects.requireNonNull(
                secondValue,
                "두 번째 대상 값은 필수입니다."
        );

        Objects.requireNonNull(
                description,
                "상호작용 설명은 필수입니다."
        );
    }
}