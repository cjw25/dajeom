package com.dajeom.model;

import com.dajeom.type.TenGodCorePatternType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * 하나의 전통 십성 Formal Pattern에 대해
 * 사용자에게 허용하는 Meaning 경계를 고정한다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 Policy는 Pattern을 새로 성립시키지 않는다.
 *
 * Pattern 존재 여부는 기존:
 *
 * TenGodCorePatternSignal
 * ->
 * TenGodCrossValidatedPattern
 * ->
 * NatalCrossFunctionalPattern
 *
 * 계산이 책임진다.
 *
 * 여기서는 실제로 성립한 Pattern에 대해
 * 어떤 Meaning까지 사용자에게 설명할 수 있는지만 정의한다.
 */
public record NatalPersonalityFormalPatternMeaningPolicy(

        TenGodCorePatternType patternType,

        String userMeaning,

        List<String> forbiddenExtensions

) {

    public NatalPersonalityFormalPatternMeaningPolicy {

        Objects.requireNonNull(
                patternType,
                "Formal Pattern Meaning Policy patternType은 필수입니다."
        );

        userMeaning =
                requireText(
                        userMeaning,
                        "Formal Pattern Meaning Policy userMeaning은 필수입니다."
                );

        forbiddenExtensions =
                normalizeTexts(
                        forbiddenExtensions
                );
    }

    public String koreanName() {

        return patternType
                .koreanName();
    }

    private static List<String> normalizeTexts(
            List<String> source
    ) {

        if (
                source == null
                        ||
                        source.isEmpty()
        ) {

            return List.of();
        }

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        for (
                String raw
                : source
        ) {

            if (
                    raw == null
            ) {

                continue;
            }

            String normalized =
                    raw.trim();

            if (
                    !normalized.isBlank()
            ) {

                result.add(
                        normalized
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}